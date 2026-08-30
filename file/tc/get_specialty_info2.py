import json
import os

import django
import pandas as pd
import requests
from bs4 import BeautifulSoup as BS, BeautifulSoup
from django.http import JsonResponse

os.environ.setdefault("DJANGO_SETTINGS_MODULE", "ScenicSpot.settings")
django.setup()
from web.models import SL_Specialty

headers = {
    'Referer': 'https://www.zhtechan.cn/shangluo/page',
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36 Edg/123.0.0.0'
}

import logging

# 创建一个文件处理器（handler）用于写入到第二个日志文件
formatter = logging.Formatter("%(asctime)s %(name)s:%(levelname)s:%(message)s", datefmt="%d-%m-%Y %H:%M:%S")
file_handler2 = logging.FileHandler("./specialty.log", mode="a", encoding="utf-8")
file_handler2.setFormatter(formatter)

# 创建另一个日志记录器（logger）并为其添加处理器
logger2 = logging.getLogger('specialty')  # 这里 'specialty' 就是另一个日志记录器的别名（或名称）
logger2.setLevel(logging.ERROR)
logger2.addHandler(file_handler2)


# print(soup)

def get_specialty_info():
    # 清空日志
    with open('D:\Python\ScenicSpot\specialty.log', 'w') as f:
        f.truncate(0)
    try:
        global specialty_info
        # 特产信息列表
        specialty_info = []
        for i in range(2):
            if i == 0:
                url = 'https://www.zhtechan.cn/shangluo/'
                res = requests.get(url=url, headers=headers)
                html = res.text
                # 页面html
                soup = BS(html, 'html.parser')
                info_dispose(soup)
            elif i == 1:
                url = 'https://www.zhtechan.cn/shangluo/page/2/'
                res = requests.get(url=url, headers=headers)
                html = res.text
                # 页面html
                soup1 = BS(html, 'html.parser')
                info_dispose(soup1)

        if os.path.exists('specialty.xlsx'):
            write_info(specialty_info)
        else:
            # 创建DataFrame，并指定列名
            df = pd.DataFrame(specialty_info,
                              columns=['特产名', '图片地址', '产地', '特产类型', '特产描述',
                                       '二级地址'])
            # 导出到Excel文件
            df.to_excel('specialty.xlsx', index=False, engine='openpyxl')
        return specialty_info
    except Exception as e:
        # 如果在执行过程中发生了任何异常，则捕获它
        print(f"错误信息: {e}")
        logger2.error(str(e))
    # return JsonResponse({'success': True, 'data': specialty_info})


def info_dispose(soup):
    try:
        all_a_label = soup.find_all("a")
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
                res = requests.get(url=link_url, headers=headers)
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
def write_info(specialty_info):
    for i in specialty_info:
        title_text, picture_url, specialty_place, specialty_type, specialty_describe, link_url = i
        SL_Specialty.objects.create(title_text=title_text,
                                 picture_url=picture_url,
                                 specialty_place=specialty_place,
                                 specialty_type=specialty_type,
                                 specialty_describe=specialty_describe,
                                 link_url=link_url)


# 京东获取特产评论
def update_specialty():
    os.environ.setdefault("DJANGO_SETTINGS_MODULE", "ScenicSpot.settings")
    django.setup()
    from web.models import SL_Specialty

    # with open('./SL_specialty.json', 'r', encoding='utf-8') as f:
    #     # 读取 JSON 文件内容
    #     data = json.load(f)

    data = {
        "洛南核桃": 10025118533741,
        "商洛丹参": 10098932414735,
        "云盖寺挂面": 1,
        "洛南豆腐": 14144402009,
        "商南茶": 10054892382970,
        "镇安象园茶": 10090859664481,
        "山阳天麻": 1,
        "柞水黑木耳": 19363534393,
        "丹凤核桃": 1,
        "山阳核桃": 10090547250030,
        "丹凤葡萄": 1,
        "柞水核桃": 1,
        "丹凤牛筋面": 10075116865699,
        "天竺山仙茗": 10084750856328,
        "商洛核桃": 10094643327209,
        "镇安大板栗": 10100735738279,
        "丹凤葡萄酒": 27889652486,
        "山阳九眼莲": 1,
        "丹凤天麻": 1,
        "商洛柿饼": 1,
        "镇安腊肉": 10025118533741,
        "柞水豆腐干": 1,
        "柞水香菇": 10066256412464,
        "洛源豆腐干": 54121421312,
        "寺坡橡子凉粉": 10035140428665,
        "象园雾芽": 10090859664481,
        "商南泉茗": 10098518177071,
        "商南香菇": 10055631187331,
        "镇安丹麻石": 1,
        "柞水板栗": 10096155533813,
        "镇安核桃": 1,
        "洛南蛮糖": 1,
        "洛南锅盔": 1,
        "木王砧板肉": 1,
        "商芝": 1,
        "镇安木耳": 1,
        "镇安香椿": 10400086892,
        "商洛黑木耳": 10048379250574,
        "镇安香菇": 1,
        "商南核桃": 10096192991904,
        "商南黑木耳": 10069781987299,
        "商洛大理石": 1,
        "商州核桃": 1,
        "商洛香菇": 10026082324118,
        "山阳龙须草": 1,
        "丹凤蕨菜": 1,
        "商南猕猴桃": 10054893875931,
        "商南花生": 1
    }
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
# print(get_specialty_info())
# #
# update_specialty()
