"""Live data collectors shared by the crawler launch scripts."""

import json
import os
import re
import time
from html import unescape
from urllib.parse import urljoin

import requests
from bs4 import BeautifulSoup


USER_AGENT = (
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
    "(KHTML, like Gecko) Chrome/124 Safari/537.36"
)
TIMEOUT_SECONDS = 20
BATCH_SIZE = 20


def create_session():
    session = requests.Session()
    session.trust_env = False
    session.headers.update({"User-Agent": USER_AGENT, "Accept-Language": "zh-CN,zh;q=0.9"})
    return session


def fetch(session, url):
    last_error = None
    for attempt in range(3):
        try:
            response = session.get(url, timeout=TIMEOUT_SECONDS)
            response.raise_for_status()
            return response
        except requests.RequestException as error:
            last_error = error
            time.sleep(attempt + 1)
    raise RuntimeError("request failed for {}: {}".format(url, last_error))


def clean_text(value):
    return re.sub(r"\s+", " ", value or "").strip()


def crawl_specialties(limit=40):
    """Collect current Qinling-region specialties from Zhonghua Techan."""
    session = create_session()
    listing_url = os.getenv("QLKY_SPECIALTY_SOURCE", "https://www.zhtechan.cn/shangluo/")
    soup = BeautifulSoup(fetch(session, listing_url).text, "html.parser")
    records = []
    visited = set()
    for link in soup.select('a[href*="/techan/"]'):
        if link.find("h2") is None:
            continue
        name = clean_text(link.get("title") or link.get_text(" ", strip=True))
        detail_url = urljoin(listing_url, link.get("href", ""))
        if not name or not detail_url or detail_url in visited:
            continue
        visited.add(detail_url)
        image = link.find("img")
        image_url = urljoin(listing_url, image.get("src", "")) if image else ""
        description = ""
        product_type = "陕西特产"
        origin_place = "商洛"
        try:
            detail_soup = BeautifulSoup(fetch(session, detail_url).text, "html.parser")
            description_meta = detail_soup.select_one('meta[name="description"]')
            description = clean_text(description_meta.get("content", "") if description_meta else "")
            for node in detail_soup.select("p"):
                line = clean_text(node.get_text(" ", strip=True))
                if "产地" in line:
                    origin_place = line.split("：")[-1].split(":")[-1].strip() or origin_place
                if "类别" in line or "类型" in line:
                    product_type = line.split("：")[-1].split(":")[-1].strip() or product_type
        except RuntimeError:
            pass
        records.append({
            "product_name": name,
            "image_url": image_url,
            "origin_place": origin_place,
            "product_type": product_type,
            "description": description or name + "，来源：中华特产网实时目录。",
            "detail_address": detail_url,
        })
        if len(records) >= limit:
            break
    if not records:
        raise RuntimeError("no specialty records found at {}".format(listing_url))
    return records


def decode_json_string(value):
    try:
        return json.loads('"' + value.replace('"', '\\"') + '"')
    except json.JSONDecodeError:
        return unescape(value).replace("\\u0026", "&")


def crawl_sights(limit=40):
    """Collect live attraction cards from Ctrip's Qinling gateway city page."""
    session = create_session()
    source_url = os.getenv("QLKY_SIGHT_SOURCE", "https://you.ctrip.com/sight/xian7.html?from=baidu")
    page = fetch(session, source_url).text
    pattern = re.compile(
        r'"poiName":"(?P<name>(?:\\.|[^"\\])*)".*?'
        r'"commentCount":(?P<review_count>\d+).*?'
        r'"commentScore":(?P<rating>[\d.]+).*?'
        r'"coverImageUrl":"(?P<image>(?:\\.|[^"\\])*)".*?'
        r'"detailUrl":"(?P<detail>(?:\\.|[^"\\])*)".*?'
        r'"price":(?P<price>\d+)',
        re.DOTALL,
    )
    records = []
    names = set()
    for match in pattern.finditer(page):
        name = decode_json_string(match.group("name"))
        if not name or name in names:
            continue
        names.add(name)
        records.append({
            "spot_name": name,
            "spot_rating": float(match.group("rating")),
            "strategy_count": None,
            "review_count": int(match.group("review_count")),
            "visitor_rate": None,
            "ranking": str(len(records) + 1),
            "secondary_address": "西安（秦岭康养辐射区）",
            "latitude": None,
            "longitude": None,
            "open_time": None,
            "location": "西安",
            "ticket_price": match.group("price"),
            "best_season": "全年",
            "image_url": decode_json_string(match.group("image")),
            "detail_url": decode_json_string(match.group("detail")),
        })
        if len(records) >= limit:
            break
    if not records:
        raise RuntimeError("no attraction records found at {}".format(source_url))
    return records


def submit_records(records, crawler_id, task_id):
    api_url = os.getenv("QLKY_API_URL", "http://127.0.0.1:9997/submitCrawlerData")
    session = create_session()
    submitted = 0
    for index in range(0, len(records), BATCH_SIZE):
        batch = records[index : index + BATCH_SIZE]
        response = session.post(api_url, json={"crawlerId": crawler_id, "taskId": task_id, "submitCrawlerList": batch}, timeout=TIMEOUT_SECONDS)
        response.raise_for_status()
        submitted += len(batch)
        print("submitted {}/{} records".format(submitted, len(records)))
    return submitted


def run(kind, crawler_id, task_id):
    collectors = {"specialty": crawl_specialties, "sight": crawl_sights}
    if kind not in collectors:
        raise ValueError("unsupported crawler kind: {}".format(kind))
    records = collectors[kind]()
    submitted = submit_records(records, crawler_id, task_id)
    print("{} crawler completed: {} live records".format(kind, submitted))
