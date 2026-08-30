import requests
import pandas as pd
import os
import glob
import json
import time
import sys
import io
from datetime import datetime

# 解决中文输出编码问题
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

# API配置
API_URL = "http://127.0.0.1:9997/submitCrawlerData"
HEADERS = {
    "Content-Type": "application/json; charset=utf-8",
    "Accept": "application/json"
}
BATCH_SIZE = 5  # 每批发送的数据量

def process_row(row):
    """处理单行数据，构建符合接口要求的字典"""
    def safe_str(val, default=""):
        return str(val).strip() if pd.notna(val) else default
    
    return {
        "product_name": safe_str(row.get("特产名")),
        "image_url": safe_str(row.get("图片地址")),
        "origin_place": safe_str(row.get("产地"), "未知地区"),
        "product_type": safe_str(row.get("特产类型"), "其他"),
        "description": safe_str(row.get("特产描述")),
        "detail_address": safe_str(row.get("二级地址")),
    }

def get_all_excel_files():
    """获取当前目录下所有Excel文件"""
    excel_files = []
    for f in glob.glob(os.path.join(os.getcwd(), "*.xlsx")):
        # 排除临时文件（文件名以~$开头）
        if not os.path.basename(f).startswith('~$'):
            excel_files.append(f)
    return excel_files

def send_batch(batch_data, batch_num, total_batches):
    """发送一批数据"""
    payload = {
        "crawlerId": '3',
        "taskId": '2',
        "submitCrawlerList": batch_data
    }
    try:
        json_str = json.dumps(payload, ensure_ascii=False)
        json.loads(json_str)  # 双重验证
    except Exception as e:
        print(f"❌ JSON生成错误: {str(e)}")
        return False

    try:
        response = requests.post(
            API_URL,
            data=json_str.encode('utf-8'),
            headers={
                "Content-Type": "application/json; charset=utf-8",
                "Content-Length": str(len(json_str))
            },
            timeout=30
        )
        response.raise_for_status()
        print(f"✅ 批次 {batch_num}/{total_batches} 发送成功 | 状态码: {response.status_code}")
        return True
    except requests.exceptions.RequestException as e:
        print(f"❌ 批次 {batch_num}/{total_batches} 发送失败: {str(e)}")
        return False

def main():
    # 获取所有Excel文件
    excel_files = get_all_excel_files()
    
    if not excel_files:
        print("\n⚠️  当前目录下未找到Excel文件！")
        print("请确认：")
        print("1. 文件扩展名为.xlsx")
        print("2. 文件不在子目录中")
        print("3. 没有Excel临时文件（以~$开头的文件）")
        return

    print(f"\n🔍 发现 {len(excel_files)} 个Excel文件：")
    for idx, f in enumerate(excel_files, 1):
        print(f"   {idx}. {os.path.basename(f)}")

    # 处理所有文件数据
    all_data = []
    total_files = len(excel_files)
    
    for file_idx, file_path in enumerate(excel_files, 1):
        try:
            print(f"\n📂 正在处理文件 ({file_idx}/{total_files}): {os.path.basename(file_path)}")
            
            # 读取Excel
            df = pd.read_excel(file_path, engine='openpyxl')
            print(f"   ├─ 读取到 {len(df)} 条原始数据")
            
            # 处理行数据
            valid_count = 0
            for _, row in df.iterrows():
                try:
                    processed = process_row(row)
                    # 基础数据校验
                    if processed["product_name"]:  # 必须有产品名称
                        all_data.append(processed)
                        valid_count += 1
                except Exception as e:
                    print(f"   ├─ 行数据处理异常: {str(e)}")
                    continue
            
            print(f"   ├─ 有效数据: {valid_count} 条")
            print(f"   └─ 无效数据: {len(df) - valid_count} 条")
            
        except Exception as e:
            print(f"🚨 文件处理失败: {os.path.basename(file_path)}")
            print(f"   错误详情: {str(e)}")
            continue

    if not all_data:
        print("\n⚠️  所有文件均无有效数据！")
        return

    # 分批发送数据
    total_count = len(all_data)
    total_batches = (total_count + BATCH_SIZE - 1) // BATCH_SIZE
    success_count = 0

    print(f"\n🚀 开始发送数据（总数据量: {total_count} 条 | 分 {total_batches} 批）")

    for i in range(0, total_count, BATCH_SIZE):
        batch_num = (i // BATCH_SIZE) + 1
        batch_data = all_data[i:i+BATCH_SIZE]

        if send_batch(batch_data, batch_num, total_batches):
            success_count += len(batch_data)

        # 添加进度延迟（最后一批不延迟）
        if batch_num < total_batches:
            time.sleep(3)

    # 输出最终结果
    print("\n📊 任务完成汇总：")
    print(f"   处理文件总数: {len(excel_files)} 个")
    print(f"   发现数据总量: {total_count} 条")
    print(f"   成功发送数量: {success_count} 条")
    print(f"   发送失败数量: {total_count - success_count} 条")
    print(f"   成功率: {success_count/total_count:.2%}")

if __name__ == "__main__":
    import sys

    sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
    from live_crawler import run

    run("specialty", int(os.getenv("QLKY_CRAWLER_ID", "3")), int(os.getenv("QLKY_TASK_ID", "2")))
