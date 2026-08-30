import json
import os

import django
import pandas as pd
import requests
from bs4 import BeautifulSoup as BS, BeautifulSoup
from django.http import JsonResponse

import web

os.environ.setdefault("DJANGO_SETTINGS_MODULE", "ScenicSpot.settings")
django.setup()
from web.models import SL_Specialty, HZ_Specialty, AK_Specialty, BJ_Specialty, XA_Specialty, WN_Specialty

headers = {
    'Referer': 'https://www.zhtechan.cn/shangluo/page',
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36 Edg/123.0.0.0'
}

import warnings
from urllib3.exceptions import InsecureRequestWarning

# 忽略InsecureRequestWarning
warnings.filterwarnings("ignore", category=InsecureRequestWarning)

import logging

# 创建一个文件处理器（handler）用于写入到第二个日志文件
formatter = logging.Formatter("%(asctime)s %(name)s:%(levelname)s:%(message)s", datefmt="%d-%m-%Y %H:%M:%S")
file_handler2 = logging.FileHandler("./specialty.log", mode="a", encoding="utf-8")
file_handler2.setFormatter(formatter)

# 创建另一个日志记录器（logger）并为其添加处理器
logger2 = logging.getLogger('specialty')  # 这里 'specialty' 就是另一个日志记录器的别名（或名称）
logger2.setLevel(logging.ERROR)
logger2.addHandler(file_handler2)

city_dict = {'shangluo': 2, 'hanzhong': 3, 'ankang': 3, 'xian': 3, 'baoji': 2, 'weinan': 3}


def get_specialty_info():
    # 启动爬虫时 清空数据库
    SL_Specialty.objects.all().delete()
    HZ_Specialty.objects.all().delete()
    AK_Specialty.objects.all().delete()
    XA_Specialty.objects.all().delete()
    BJ_Specialty.objects.all().delete()
    WN_Specialty.objects.all().delete()
    # 清空日志
    with open('D:\Python\ScenicSpot\specialty.log', 'w') as f:
        f.truncate(0)
    try:
        global specialty_info
        # 特产信息列表
        for city in city_dict.items():
            specialty_info = []
            for i in range(1, int(city[1]) + 1):
                if i == 1:
                    url = f'https://www.zhtechan.cn/{city[0]}/'
                    res = requests.get(url=url, verify=False, headers=headers)
                    html = res.text
                    # 页面html
                    soup = BS(html, 'html.parser')
                    info_dispose(soup)
                else:
                    url = f'https://www.zhtechan.cn/{city[0]}/page/{i}/'
                    res = requests.get(url=url, verify=False, headers=headers)
                    html = res.text
                    # 页面html
                    soup1 = BS(html, 'html.parser')
                    info_dispose(soup1)
            if os.path.exists(f'D:\Python\ScenicSpot\web\specialty_xlsx/{city[0]}.xlsx'):
                write_info(specialty_info, city[0])
                df = pd.DataFrame(specialty_info,
                                  columns=['特产名', '图片地址', '产地', '特产类型', '特产描述',
                                           '二级地址'])
                # 导出到Excel文件
                df.to_excel(f'D:\Python\ScenicSpot\web\specialty_xlsx/{city[0]}.xlsx', index=False, engine='openpyxl')
            else:
                # 创建DataFrame，并指定列名
                df = pd.DataFrame(specialty_info,
                                  columns=['特产名', '图片地址', '产地', '特产类型', '特产描述',
                                           '二级地址'])
                # 导出到Excel文件
                df.to_excel(f'D:\Python\ScenicSpot\web\specialty_xlsx/{city[0]}.xlsx', index=False, engine='openpyxl')
                write_info(specialty_info, city[0])
            # return specialty_info

    except Exception as e:
        # 如果在执行过程中发生了任何异常，则捕获它
        print(f"错误信息: {e}")
        logger2.error(str(e))


def info_dispose(soup):
    try:
        all_a_label = soup.find_all("a")
        # print(all_a_label)
        for link in all_a_label:
            # 二级详细页地址
            link_url = link['href']
            # 图片地址
            picture_url = link.find('img')['src'] if link.find('img') else None
            # 查找当前<a>标签内部的<h2>标签并提取文本内容
            title_text = link.find('h2').text.strip() if link.find('h2') else None
            # 打印结果
            # print(link_url)
            if title_text:
                # 特产信息
                res = requests.get(url=link_url, headers=headers, verify=False)
                html = res.text
                # 页面html
                soup = BS(html, 'html.parser')
                # 特产产地
                specialty_place = soup.find_all(name="p", attrs={"class": "fs-3 text-white-50"})[-2].get_text()[3:]
                print(specialty_place)

                # 特产类型
                specialty_type = soup.find_all(name="p", attrs={"class": "fs-3 text-white-50"})[-1].get_text()[3:]
                # print(specialty_type)

                # 特产描述
                specialty_describe = soup.find_all(name="p", attrs={"class": "fs-3 text-white-50"})[
                    -3].get_text().strip()
                # print(specialty_describe)
                # print("二级链接地址:", link_url)
                # print("特产名:", title_text)
                # print("图片地址:", picture_url)
                specialty_info.append(
                    (title_text, picture_url, specialty_place, specialty_type, specialty_describe, link_url))
    except Exception as e:
        # 如果在执行过程中发生了任何异常，则捕获它
        print(f"错误信息: {e}")
        logger2.error(str(e))


# 写入数据库函数
def write_info(specialty_info, city):
    for i in specialty_info:
        title_text, picture_url, specialty_place, specialty_type, specialty_describe, link_url = i
        if city == 'shangluo':
            SL_Specialty.objects.create(title_text=title_text,
                                        picture_url=picture_url,
                                        specialty_place=specialty_place,
                                        specialty_type=specialty_type,
                                        specialty_describe=specialty_describe,
                                        link_url=link_url)
        elif city == 'hanzhong':

            HZ_Specialty.objects.create(title_text=title_text,
                                        picture_url=picture_url,
                                        specialty_place=specialty_place,
                                        specialty_type=specialty_type,
                                        specialty_describe=specialty_describe,
                                        link_url=link_url)
        elif city == 'ankang':

            AK_Specialty.objects.create(title_text=title_text,
                                        picture_url=picture_url,
                                        specialty_place=specialty_place,
                                        specialty_type=specialty_type,
                                        specialty_describe=specialty_describe,
                                        link_url=link_url)
        elif city == 'xian':

            XA_Specialty.objects.create(title_text=title_text,
                                        picture_url=picture_url,
                                        specialty_place=specialty_place,
                                        specialty_type=specialty_type,
                                        specialty_describe=specialty_describe,
                                        link_url=link_url)
        elif city == 'baoji':

            BJ_Specialty.objects.create(title_text=title_text,
                                        picture_url=picture_url,
                                        specialty_place=specialty_place,
                                        specialty_type=specialty_type,
                                        specialty_describe=specialty_describe,
                                        link_url=link_url)
        elif city == 'weinan':

            WN_Specialty.objects.create(title_text=title_text,
                                        picture_url=picture_url,
                                        specialty_place=specialty_place,
                                        specialty_type=specialty_type,
                                        specialty_describe=specialty_describe,
                                        link_url=link_url)


# 京东获取特产评论
def update_specialty():
    os.environ.setdefault("DJANGO_SETTINGS_MODULE", "ScenicSpot.settings")
    django.setup()
    for city in city_dict.items():
        if city[0] == 'shangluo':
            # 定义JSON文件的路径
            json_file_path = f'D:\Python\ScenicSpot\web\specialty_json\SL_specialty.json'
            # 使用with语句打开文件并读取JSON数据
            with open(json_file_path, 'r', encoding='utf-8') as json_file:  # 确保指定正确的编码，如'utf-8'
                data = json.load(json_file)
            try:
                for i in data.items():
                    specialty_name = i[0]
                    specialty_link = i[1]
                    url = (
                        f'https://club.jd.com/comment/productPageComments.action?&productId={specialty_link}&score=0&sortType=5'
                        f'&page=0&pageSize=10000&isShadowSku=0&fold=1')
                    res = requests.get(url=url)
                    info = []
                    for i in json.loads(res.text)['comments']:
                        info.append(i['content'])
                    SL_Specialty.objects.filter(title_text=specialty_name).update(comment=list(info))
            except Exception as e:
                # 如果在执行过程中发生了任何异常，则捕获它
                print(f"错误信息: {e}")
                logger2.error(str(e))
        elif city[0] == 'ankang':
            # 定义JSON文件的路径
            json_file_path = f'D:\Python\ScenicSpot\web\specialty_json\AK_specialty.json'
            # 使用with语句打开文件并读取JSON数据
            with open(json_file_path, 'r', encoding='utf-8') as json_file:  # 确保指定正确的编码，如'utf-8'
                data = json.load(json_file)
            try:
                for i in data.items():
                    specialty_name = i[0]
                    specialty_link = i[1]
                    url = (
                        f'https://club.jd.com/comment/productPageComments.action?&productId={specialty_link}&score=0&sortType=5'
                        f'&page=0&pageSize=10000&isShadowSku=0&fold=1')
                    res = requests.get(url=url)
                    info = []
                    for i in json.loads(res.text)['comments']:
                        info.append(i['content'])
                    AK_Specialty.objects.filter(title_text=specialty_name).update(comment=list(info))
            except Exception as e:
                # 如果在执行过程中发生了任何异常，则捕获它
                print(f"错误信息: {e}")
                logger2.error(str(e))

        elif city[0] == 'hanzhong':
            # 定义JSON文件的路径
            json_file_path = f'D:\Python\ScenicSpot\web\specialty_json\HZ_specialty.json'
            # 使用with语句打开文件并读取JSON数据
            with open(json_file_path, 'r', encoding='utf-8') as json_file:  # 确保指定正确的编码，如'utf-8'
                data = json.load(json_file)
            try:
                for i in data.items():
                    specialty_name = i[0]
                    specialty_link = i[1]
                    url = (
                        f'https://club.jd.com/comment/productPageComments.action?&productId={specialty_link}&score=0&sortType=5'
                        f'&page=0&pageSize=10000&isShadowSku=0&fold=1')
                    res = requests.get(url=url)
                    info = []
                    for i in json.loads(res.text)['comments']:
                        info.append(i['content'])
                    HZ_Specialty.objects.filter(title_text=specialty_name).update(comment=list(info))
            except Exception as e:
                # 如果在执行过程中发生了任何异常，则捕获它
                print(f"错误信息: {e}")
                logger2.error(str(e))
        elif city[0] == 'xian':
            # 定义JSON文件的路径
            json_file_path = f'D:\Python\ScenicSpot\web\specialty_json\XA_specialty.json'
            # 使用with语句打开文件并读取JSON数据
            with open(json_file_path, 'r', encoding='utf-8') as json_file:  # 确保指定正确的编码，如'utf-8'
                data = json.load(json_file)
            try:
                for i in data.items():
                    specialty_name = i[0]
                    specialty_link = i[1]
                    url = (
                        f'https://club.jd.com/comment/productPageComments.action?&productId={specialty_link}&score=0&sortType=5'
                        f'&page=0&pageSize=10000&isShadowSku=0&fold=1')
                    res = requests.get(url=url)
                    info = []
                    for i in json.loads(res.text)['comments']:
                        info.append(i['content'])
                    XA_Specialty.objects.filter(title_text=specialty_name).update(comment=list(info))
            except Exception as e:
                # 如果在执行过程中发生了任何异常，则捕获它
                print(f"错误信息: {e}")
                logger2.error(str(e))
        elif city[0] == 'baoji':
            # 定义JSON文件的路径
            json_file_path = f'D:\Python\ScenicSpot\web\specialty_json\BJ_specialty.json'
            # 使用with语句打开文件并读取JSON数据
            with open(json_file_path, 'r', encoding='utf-8') as json_file:  # 确保指定正确的编码，如'utf-8'
                data = json.load(json_file)
            try:
                for i in data.items():
                    specialty_name = i[0]
                    specialty_link = i[1]
                    url = (
                        f'https://club.jd.com/comment/productPageComments.action?&productId={specialty_link}&score=0&sortType=5'
                        f'&page=0&pageSize=10000&isShadowSku=0&fold=1')
                    res = requests.get(url=url)
                    info = []
                    for i in json.loads(res.text)['comments']:
                        info.append(i['content'])
                    BJ_Specialty.objects.filter(title_text=specialty_name).update(comment=list(info))
            except Exception as e:
                # 如果在执行过程中发生了任何异常，则捕获它
                print(f"错误信息: {e}")
                logger2.error(str(e))

        elif city[0] == 'weinan':
            # 定义JSON文件的路径
            json_file_path = f'D:\Python\ScenicSpot\web\specialty_json\WN_specialty.json'
            # 使用with语句打开文件并读取JSON数据
            with open(json_file_path, 'r', encoding='utf-8') as json_file:  # 确保指定正确的编码，如'utf-8'
                data = json.load(json_file)
            try:
                for i in data.items():
                    specialty_name = i[0]
                    specialty_link = i[1]
                    url = (
                        f'https://club.jd.com/comment/productPageComments.action?&productId={specialty_link}&score=0&sortType=5'
                        f'&page=0&pageSize=10000&isShadowSku=0&fold=1')
                    res = requests.get(url=url)
                    info = []
                    for i in json.loads(res.text)['comments']:
                        info.append(i['content'])
                    WN_Specialty.objects.filter(title_text=specialty_name).update(comment=list(info))
            except Exception as e:
                # 如果在执行过程中发生了任何异常，则捕获它
                print(f"错误信息: {e}")
                logger2.error(str(e))


print(get_specialty_info())
# #
update_specialty()
