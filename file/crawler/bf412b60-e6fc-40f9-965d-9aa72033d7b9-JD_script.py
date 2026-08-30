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
    return {
        "spot_name": str(row.get("景点名称", "")),
        "spot_rating": safe_int(row.get("景点评分")),
        "strategy_count": safe_int(row.get("景点攻略数")),
        "review_count": safe_int(row.get("景点点评数")),
        "visitor_rate": safe_float(row.get("景点驴友到访率")),
        "ranking": safe_str(row.get("景点排名")),
        "secondary_address": safe_str(row.get("二级地址")),
        "latitude": safe_str(row.get("纬度")),
        "longitude": safe_str(row.get("经度")),
        "open_time": safe_str(row.get("开放时间")),
        "location": safe_str(row.get("位置")),
        "ticket_price": safe_str(row.get("门票价格")),
        "best_season": safe_str(row.get("旅游时节")),
        "image_url": safe_str(row.get("图片地址")),
    }

def safe_int(value):
    """安全转换为整数"""
    try:
        return int(float(value)) if pd.notna(value) and str(value).strip() != "" else None
    except:
        return None

def safe_float(value):
    """安全转换为浮点数（处理百分比）"""
    try:
        if pd.notna(value) and str(value).strip() != "":
            clean_str = str(value).replace("%", "").strip()
            return float(clean_str) / 100 if "%" in str(value) else float(clean_str)
        return None
    except:
        return None

def safe_str(value):
    """安全转换为字符串"""
    return str(value) if pd.notna(value) and str(value).strip() != "" else None

def get_excel_files():
    """获取当前目录下所有Excel文件"""
    files = []
    for f in glob.glob(os.path.join(os.getcwd(), "*.xlsx")):
        # 排除临时文件
        if not os.path.basename(f).startswith('~$'):
            files.append(f)
    return files

def validate_data(data_list):
    """数据验证"""
    valid_data = []
    for item in data_list:
        # 必须有景点名称
        if not item.get('spot_name'):
            continue
        # 至少包含一个有效数值字段
        if all(v is None for k, v in item.items() if k != 'spot_name'):
            continue
        valid_data.append(item)
    return valid_data

def send_batch(batch_data, batch_num, total_batches):
    """发送数据批次"""
    payload = {
        "crawlerId": "2",
        "taskId": "1",
        "submitCrawlerList": batch_data
    }
    
    try:
        # 双重JSON验证
        json_str = json.dumps(payload, ensure_ascii=False)
        json.loads(json_str)
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

def process_files(file_list):
    """处理文件列表"""
    all_data = []
    total_files = len(file_list)
    
    for idx, file_path in enumerate(file_list, 1):
        try:
            print(f"\n📂 正在处理文件 ({idx}/{total_files}): {os.path.basename(file_path)}")
            
            # 读取Excel
            df = pd.read_excel(file_path, engine='openpyxl')
            print(f"   ├─ 发现 {len(df)} 条原始数据")
            
            # 处理数据
            file_data = []
            for _, row in df.iterrows():
                try:
                    file_data.append(process_row(row))
                except Exception as e:
                    print(f"   ├─ 行数据处理异常: {str(e)}")
                    continue
            
            # 数据验证
            valid_data = validate_data(file_data)
            invalid_count = len(file_data) - len(valid_data)
            
            print(f"   ├─ 有效数据: {len(valid_data)} 条")
            print(f"   ├─ 无效数据: {invalid_count} 条")
            print(f"   └─ 完成进度: {len(valid_data)}/{len(df)}")
            
            all_data.extend(valid_data)
            
        except Exception as e:
            print(f"🚨 文件处理异常: {os.path.basename(file_path)} - {str(e)}")
            continue
    
    return all_data

def main():
    # 获取文件列表
    excel_files = get_excel_files()
    
    if not excel_files:
        print("\n⚠️  未找到Excel文件！请确认：")
        print("   1. 文件扩展名为.xlsx")
        print("   2. 文件不在其他子目录")
        print("   3. 没有Excel临时文件（以~$开头的文件）")
        return
    
    print(f"\n🔍 发现 {len(excel_files)} 个Excel文件：")
    for i, f in enumerate(excel_files, 1):
        print(f"   {i}. {os.path.basename(f)}")
    
    # 处理所有文件
    start_time = time.time()
    all_data = process_files(excel_files)
    
    if not all_data:
        print("\n⚠️  所有文件均无有效数据！")
        return
    
    # 分批发送
    total_count = len(all_data)
    total_batches = (total_count + BATCH_SIZE - 1) // BATCH_SIZE
    success_count = 0
    
    print(f"\n🚀 开始发送数据（总数据量: {total_count} 条 | 分 {total_batches} 批）")
    
    for i in range(0, total_count, BATCH_SIZE):
        batch_num = (i // BATCH_SIZE) + 1
        batch = all_data[i:i+BATCH_SIZE]
        
        if send_batch(batch, batch_num, total_batches):
            success_count += len(batch)
        
        # 进度控制
        if batch_num < total_batches:
            time.sleep(3)  # 防止服务器过载
    
    # 输出统计结果
    time_cost = time.time() - start_time
    print(f"\n📊 任务完成统计:")
    print(f"   ├─ 总耗时: {time_cost:.2f}秒")
    print(f"   ├─ 总文件数: {len(excel_files)} 个")
    print(f"   ├─ 总数据量: {total_count} 条")
    print(f"   ├─ 成功发送: {success_count} 条")
    print(f"   └─ 失败数量: {total_count - success_count} 条")

if __name__ == "__main__":
    sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
    from live_crawler import run

    run("sight", int(os.getenv("QLKY_CRAWLER_ID", "2")), int(os.getenv("QLKY_TASK_ID", "1")))
