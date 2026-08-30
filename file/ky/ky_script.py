import os
import xml.etree.ElementTree as ET
import requests
import json
import time
import sys
import io

sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

# 全局配置
API_URL = "http://127.0.0.1:9997/submitCrawlerData"
HEADERS = {
    "Content-Type": "application/json; charset=utf-8",
    "Accept": "application/json"
}
BATCH_SIZE = 5

def get_xml_files():
    """获取当前目录下所有XML文件"""
    xml_files = []
    for filename in os.listdir('.'):
        if filename.lower().endswith('.xml') and not filename.startswith('~$'):
            xml_files.append(filename)
    return sorted(xml_files)

def parse_single_xml(xml_path):
    """解析单个XML文件"""
    try:
        tree = ET.parse(xml_path)
        root = tree.getroot()
        records = []

        for record in root.findall('RECORD'):
            item = {
                'shop_id': safe_str(record.findtext('店铺id')),
                'shop_name': safe_str(record.findtext('店铺名')),
                'review_count': safe_int(record.findtext('评论总数')),
                'avg_price': safe_int(record.findtext('人均价格')),
                'category': safe_str(record.findtext('项目')),
                'location': safe_str(record.findtext('所在地')),
                'address': safe_str(record.findtext('店铺地址')),
                'detail_url': safe_str(record.findtext('详情链接')),
                'image_url': safe_str(record.findtext('图片链接')),
                'overall_score': safe_float(record.findtext('店铺总分')),
                'phone': safe_str(record.findtext('店铺电话')),
                'latitude': safe_float(record.findtext('店铺纬度')),
                'longitude': safe_float(record.findtext('店铺经度')),
                'province': safe_str(record.findtext('省份'))
            }

            # 解析评分JSON
            score_json = safe_str(record.findtext('店铺均分'))
            if score_json:
                try:
                    scores = json.loads(score_json)
                    item.update({
                        'taste_score': safe_float(scores.get('口味')),
                        'environment_score': safe_float(scores.get('环境')),
                        'service_score': safe_float(scores.get('服务'))
                    })
                except json.JSONDecodeError:
                    pass

            records.append(item)
            
        return records
    except Exception as e:
        print(f"解析 {xml_path} 失败: {str(e)}")
        return []

def safe_str(value):
    """安全字符串处理"""
    if value is None:
        return ''
    return str(value).strip()

def safe_int(value):
    """安全整数转换"""
    try:
        return int(float(value.strip())) if value and value.strip() not in ['', '-'] else None
    except:
        return None

def safe_float(value):
    """安全浮点数转换"""
    try:
        return float(value.strip()) if value and value.strip() not in ['', '-'] else None
    except:
        return None

def validate_record(record):
    """数据验证"""
    if not record.get('shop_name'):
        return False
    required_fields = ['review_count', 'avg_price', 'overall_score']
    return any(record.get(field) is not None for field in required_fields)

def transform_data(raw_data):
    """数据格式转换"""
    transformed = []
    for item in raw_data:
        if validate_record(item):
            transformed.append({
                "shop_name": item['shop_name'],
                "shop_id": item['shop_id'],
                "review_count": item['review_count'],
                "avg_price": item['avg_price'],
                "category": item['category'],
                "location": item['location'],
                "address": item['address'],
                "detail_url": item['detail_url'],
                "image_url": item['image_url'],
                "latitude": item['latitude'],
                "longitude": item['longitude'],
                "contact": item['phone'],
                "province": item['province'],
                "overall_score": item['overall_score'],
                'taste_score': item['taste_score'],
                'environment_score': item['environment_score'],
                'service_score':item['service_score']
            })
    return transformed

def send_data_batch(batch_data, batch_num, total_batches):
    """发送数据批次"""
    payload = {
        "crawlerId": "4",
        "taskId": "3",
        "submitCrawlerList": batch_data
    }

    try:
        response = requests.post(
            API_URL,
            json=payload,
            headers=HEADERS,
            timeout=15
        )
        response.raise_for_status()
        print(f"✅ 批次 {batch_num}/{total_batches} 发送成功")
        return True
    except requests.exceptions.RequestException as e:
        print(f"❌ 批次 {batch_num}/{total_batches} 发送失败: {str(e)}")
        return False
    except Exception as e:
        print(f"❌ 未知错误: {str(e)}")
        return False

def process_files(xml_files):
    """处理文件集合"""
    total_success = 0
    total_records = 0
    file_count = len(xml_files)

    for index, xml_file in enumerate(xml_files, 1):
        print(f"\n📂 正在处理文件 ({index}/{file_count}): {xml_file}")
        
        # 解析数据
        raw_data = parse_single_xml(xml_file)
        if not raw_data:
            continue
            
        # 数据转换
        valid_data = transform_data(raw_data)
        valid_count = len(valid_data)
        invalid_count = len(raw_data) - valid_count
        
        print(f"   ├─ 原始数据: {len(raw_data)} 条")
        print(f"   ├─ 有效数据: {valid_count} 条")
        print(f"   ├─ 无效数据: {invalid_count} 条")

        if not valid_data:
            continue

        # 分批发送
        batch_total = len(valid_data)
        total_batches = (batch_total + BATCH_SIZE - 1) // BATCH_SIZE
        batch_success = 0

        print(f"\n🚀 开始发送数据（本文件 {batch_total} 条 | 分 {total_batches} 批）")

        for i in range(0, batch_total, BATCH_SIZE):
            batch_num = (i // BATCH_SIZE) + 1
            batch = valid_data[i:i+BATCH_SIZE]
            
            if send_data_batch(batch, batch_num, total_batches):
                batch_success += len(batch)

            # 请求间隔
            if batch_num < total_batches:
                time.sleep(1.5)

        # 更新统计
        total_success += batch_success
        total_records += batch_total

        print(f"📊 本文件完成: 成功 {batch_success}/{batch_total}")

    return total_success, total_records

def main():
    """主执行流程"""
    start_time = time.time()
    
    # 获取文件列表
    xml_files = get_xml_files()
    if not xml_files:
        print("⚠️ 未发现任何XML文件")
        return

    print(f"🔍 发现 {len(xml_files)} 个XML文件:")
    for i, f in enumerate(xml_files, 1):
        print(f"  {i}. {f}")

    # 处理所有文件
    success_total, records_total = process_files(xml_files)

    # 输出统计
    time_cost = time.time() - start_time
    print("\n" + "="*50)
    print("📊 全局统计结果:")
    print(f"├─ 处理文件总数: {len(xml_files)} 个")
    print(f"├─ 总数据量: {records_total} 条")
    print(f"├─ 成功发送: {success_total} 条")
    print(f"├─ 失败数量: {records_total - success_total} 条")
    print(f"├─ 成功率: {success_total/records_total:.1%}" if records_total else "├─ 成功率: N/A")
    print(f"└─ 总耗时: {time_cost:.2f} 秒")

if __name__ == "__main__":
    main()