# # -*- coding: utf-8 -*-
# import logging
#
# import django
# import re
#
# import httpx
# import pandas as pd
# from bs4 import BeautifulSoup as BS
# import requests, os
#
# os.environ.setdefault("DJANGO_SETTINGS_MODULE", "ScenicSpot.settings")
# django.setup()
# from web.models import ScenicSpot
# import time
#
# # 创建一个格式器（formatter）来定义日志消息的格式
# formatter = logging.Formatter("%(asctime)s %(name)s:%(levelname)s:%(message)s", datefmt="%d-%m-%Y %H:%M:%S")
#
# # 创建一个文件处理器（handler）用于写入到第一个日志文件
# file_handler1 = logging.FileHandler("./scenic_spot.log", mode="a", encoding="utf-8")
# file_handler1.setFormatter(formatter)
#
# # 创建一个日志记录器（logger）并为其添加处理器
# logger1 = logging.getLogger('scenic_spot')  # 这里 'scenic_spot' 就是日志记录器的别名（或名称）
# logger1.setLevel(logging.ERROR)
# logger1.addHandler(file_handler1)
#
#
# # print(soup)
#
#
# def request(url):
#     import requests
#     # time.sleep(2)
#     # 隧道域名:端口号
#     # tunnel = "x328.kdltps.com:15818"
#     #
#     # # 用户名密码方式
#     # username = "t11447114412086"
#     # password = "lf8on70c"
#     # proxies = {
#     #     "http": "http://%(user)s:%(pwd)s@%(proxy)s/" % {"user": username, "pwd": password, "proxy": tunnel},
#     #     "https": "http://%(user)s:%(pwd)s@%(proxy)s/" % {"user": username, "pwd": password, "proxy": tunnel}
#     # }
#     # print(proxies)
#     # 请求头 浏览器类型
#     headers = {
#         'cookie': "QN1=0000ed00306c5e5e04b0105b; QN300=s%3Dbing; QN99=3007; "
#                   "QunarGlobal=10.68.204.216_-51a614aa_18ec23e4258_47ee|1712667287399; qunar-assist={"
#                   "%22version%22:%2220211215173359.925%22%2C%22show%22:false%2C%22audio%22:false%2C%22speed%22:%22middle"
#                   "%22%2C%22zomm%22:1%2C%22cursor%22:false%2C%22pointer%22:false%2C%22bigtext%22:false%2C%22overead%22"
#                   ":false%2C%22readscreen%22:false%2C%22theme%22:%22default%22}; QN205=s%3Dbing; QN277=s%3Dbing; "
#                   "QN601=850c50abdb872d606fac01be807764d8; QN163=0; QN269=58D72991F67011EEB9F466D5AF01ED31; "
#                   "fid=2fd393f4-6de1-432d-9f6e-22b5ce676971; QN48=01ea462e-3fca-4280-ae20-247593c3016c; activityClose=1; "
#                   "QN243=11; ariaDefaultTheme=null; Hm_lvt_c56a2b5278263aa647778d304009eafc=1712667345; "
#                   "QN57=17126684836830.24124188035790217; QN58=1712668483681%7C1712669312848%7C5; "
#                   "ctt_june=1683616182042##iK3wWRaAWuPwawPwa%3DXnasEIWRamXsDsWsPAaSvAWS2sVPjNa2GREDP"
#                   "%3DVRWRiK3siK3saKgnaSjOVKj%2BasgOVhPwaUvt; "
#                   "QN19"
#                   "=ZpeEUz5onPd5MI3zNtDw1_oH3wjWDvSriLMnEF6qmXx2UzOAcWfVomrsvbhtgr16OHUm6Mf09Kses1RtTE3YJ3sl6V4Ate6Stbf39534edded1f920d70f29b26fe50a561932291021; _challenge=f1c76e20-ca1a-423c-8b2c-7c840caa86a9; QN25=6233aa87-27cb-4503-a5e0-4686b80204ad-9f992f90; QN271AC=register_pc; QN271SL=86d881513e16e96885686051c38d06a5; QN271RC=86d881513e16e96885686051c38d06a5; _q=U.ilkkwun7611; csrfToken=PkZdu1yGbbogwTiKWTWqWlcarFobIvNd; _s=s_ZLGCFJBVIJELCUHCIHQWABX7CU; _t=28653935; _v=PZ675nWyVLsvf2XUiPERRrdNAaQo-GrCSwEbMqzRp0ruROIsQu13P6SYnouK6ysok0sD1Snh8Roh9SvAmAjHVX1IOEdQT2EyqKwOzhzS5DdElWexXt8Ml4BoYWTJYCybvwl20f9_SODxVCoJ4JxYHEwjeMEA7RlIpT0ikP8KxY0J; QN43=""; QN42=%E5%8E%BB%E5%93%AA%E5%84%BF%E7%94%A8%E6%88%B7; QN44=ilkkwun7611; _i=DFiEuM8wzPfwDD5etzoAHwsPVNNw; HN1=v17d39b96d0b04c60794eb7c8fce16f1d5; HN2=qkqqssrssluqk; ctf_june=1683616182042##iK3waS2OawPwawPwaskTXPXAVKtsXPGhEKjNaRDwWs0TVRGTX%3DaOWsPmWPD%2BiK3siK3saKgnaSjOVKgsVKamVhPwaUvt; cs_june=baa1ad377fe4ed45f596fe0737c05fee58f811a5907ce994447e931556a4d095888c17f65937d8bdd871d7817ce74418df2d80fe7a1fcbe11b1241543bffcba6b17c80df7eee7c02a9c1a6a5b97c1179df6b01d31d7b0f6eb75fdfa419183a2b5a737ae180251ef5be23400b098dd8ca; _vi=3KInkExyDDaDVLKWhPs4fSpaWAz7XN8CWq0XB_gWCq8xtXm9w4e4l3cMyyK7t0Bm4Epr9HDb_3Zejo09g35OPT8x8yvZs1epfR0CqpTtfTbKsjAiIvb4H5JYBgz3ZkJ878-OcmqqhNTQAsKBsOp1OfsyU6MyPlfUVwIjUfAwNSbF; viewdist=300197-21|300100-4|299878-7; uld=1-300197-21-1712671381|1-299878-7-1712671010|1-300100-4-1712667574; JSESSIONID=032F62C08DF0AE017A82EFA387C777AD; QN267=1774469831bea30784; Hm_lpvt_c56a2b5278263aa647778d304009eafc=1712671382; QN271=d3b33346-d291-429c-89e3-c182d32763bb; SECKEY_ABVK=zDM80QGvISIbm1xZF0Ow8Bal2KQGeJz3IFmE1msAxe4%3D; BMAP_SECKEY=zDM80QGvISIbm1xZF0Ow8IR6djKYyFPnY-h9NHFbS0ihM3IQy_LtnVRXrSmvLaw1_bvhXYBujDKVellfjAKnBm7YFg8hthvCID9HJt63WhxAWMpUwZhgIMW9PC5TDlGNZrNtO7t8irx9ZXpltNY-rQupQ6TsAkCUYoHOaRF4P96bcvUpJOAwQf8JFiRuhocx",
#         "user-agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) "
#                       "Chrome/123.0.0.0 Safari/537.36 Edg/123.0.0.0",
#         "Referer": "https://travel.qunar.com/p-cs300197-shangluo"}
#     # 向网址发送请求
#     res = requests.get(url, headers=headers)
#     html = res.text
#
#     # 页面html
#     soup = BS(html, 'html.parser')
#     # print(soup)
#
#     # 城市对应景点页数总数
#     toal_pages = soup.find_all(name="a", attrs={"class": "page"})[-2].get_text()
#     print(f'该城市共有 {toal_pages} 页景点')
#     return soup, toal_pages
#
#
# def request2(url):
#     import requests
#     time.sleep(1)
#     tunnel = "t221.kdltpspro.com:15818"
#
#     # 用户名和密码方式
#     username = "t11448059300190"
#     password = "6moakyt4"
#
#     proxy_url = "http://%(user)s:%(pwd)s@%(proxy)s/" % {"user": username, "pwd": password, "proxy": tunnel}
#
#     proxies = httpx.Proxy(
#         url=proxy_url
#     )
#     # 请求头 浏览器类型
#     headers = {
#         'cookie': "QN1=0000ed00306c5e5e04b0105b; QN300=s%3Dbing; QN99=3007; "
#                   "QunarGlobal=10.68.204.216_-51a614aa_18ec23e4258_47ee|1712667287399; qunar-assist={"
#                   "%22version%22:%2220211215173359.925%22%2C%22show%22:false%2C%22audio%22:false%2C%22speed%22:%22middle"
#                   "%22%2C%22zomm%22:1%2C%22cursor%22:false%2C%22pointer%22:false%2C%22bigtext%22:false%2C%22overead%22"
#                   ":false%2C%22readscreen%22:false%2C%22theme%22:%22default%22}; QN205=s%3Dbing; QN277=s%3Dbing; "
#                   "QN601=850c50abdb872d606fac01be807764d8; QN163=0; QN269=58D72991F67011EEB9F466D5AF01ED31; "
#                   "fid=2fd393f4-6de1-432d-9f6e-22b5ce676971; QN48=01ea462e-3fca-4280-ae20-247593c3016c; activityClose=1; "
#                   "QN243=11; ariaDefaultTheme=null; Hm_lvt_c56a2b5278263aa647778d304009eafc=1712667345; "
#                   "QN57=17126684836830.24124188035790217; QN58=1712668483681%7C1712669312848%7C5; "
#                   "ctt_june=1683616182042##iK3wWRaAWuPwawPwa%3DXnasEIWRamXsDsWsPAaSvAWS2sVPjNa2GREDP"
#                   "%3DVRWRiK3siK3saKgnaSjOVKj%2BasgOVhPwaUvt; "
#                   "QN19"
#                   "=ZpeEUz5onPd5MI3zNtDw1_oH3wjWDvSriLMnEF6qmXx2UzOAcWfVomrsvbhtgr16OHUm6Mf09Kses1RtTE3YJ3sl6V4Ate6Stbf39534edded1f920d70f29b26fe50a561932291021; _challenge=f1c76e20-ca1a-423c-8b2c-7c840caa86a9; QN25=6233aa87-27cb-4503-a5e0-4686b80204ad-9f992f90; QN271AC=register_pc; QN271SL=86d881513e16e96885686051c38d06a5; QN271RC=86d881513e16e96885686051c38d06a5; _q=U.ilkkwun7611; csrfToken=PkZdu1yGbbogwTiKWTWqWlcarFobIvNd; _s=s_ZLGCFJBVIJELCUHCIHQWABX7CU; _t=28653935; _v=PZ675nWyVLsvf2XUiPERRrdNAaQo-GrCSwEbMqzRp0ruROIsQu13P6SYnouK6ysok0sD1Snh8Roh9SvAmAjHVX1IOEdQT2EyqKwOzhzS5DdElWexXt8Ml4BoYWTJYCybvwl20f9_SODxVCoJ4JxYHEwjeMEA7RlIpT0ikP8KxY0J; QN43=""; QN42=%E5%8E%BB%E5%93%AA%E5%84%BF%E7%94%A8%E6%88%B7; QN44=ilkkwun7611; _i=DFiEuM8wzPfwDD5etzoAHwsPVNNw; HN1=v17d39b96d0b04c60794eb7c8fce16f1d5; HN2=qkqqssrssluqk; ctf_june=1683616182042##iK3waS2OawPwawPwaskTXPXAVKtsXPGhEKjNaRDwWs0TVRGTX%3DaOWsPmWPD%2BiK3siK3saKgnaSjOVKgsVKamVhPwaUvt; cs_june=baa1ad377fe4ed45f596fe0737c05fee58f811a5907ce994447e931556a4d095888c17f65937d8bdd871d7817ce74418df2d80fe7a1fcbe11b1241543bffcba6b17c80df7eee7c02a9c1a6a5b97c1179df6b01d31d7b0f6eb75fdfa419183a2b5a737ae180251ef5be23400b098dd8ca; _vi=3KInkExyDDaDVLKWhPs4fSpaWAz7XN8CWq0XB_gWCq8xtXm9w4e4l3cMyyK7t0Bm4Epr9HDb_3Zejo09g35OPT8x8yvZs1epfR0CqpTtfTbKsjAiIvb4H5JYBgz3ZkJ878-OcmqqhNTQAsKBsOp1OfsyU6MyPlfUVwIjUfAwNSbF; viewdist=300197-21|300100-4|299878-7; uld=1-300197-21-1712671381|1-299878-7-1712671010|1-300100-4-1712667574; JSESSIONID=032F62C08DF0AE017A82EFA387C777AD; QN267=1774469831bea30784; Hm_lpvt_c56a2b5278263aa647778d304009eafc=1712671382; QN271=d3b33346-d291-429c-89e3-c182d32763bb; SECKEY_ABVK=zDM80QGvISIbm1xZF0Ow8Bal2KQGeJz3IFmE1msAxe4%3D; BMAP_SECKEY=zDM80QGvISIbm1xZF0Ow8IR6djKYyFPnY-h9NHFbS0ihM3IQy_LtnVRXrSmvLaw1_bvhXYBujDKVellfjAKnBm7YFg8hthvCID9HJt63WhxAWMpUwZhgIMW9PC5TDlGNZrNtO7t8irx9ZXpltNY-rQupQ6TsAkCUYoHOaRF4P96bcvUpJOAwQf8JFiRuhocx",
#         "user-agent": "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.9 "
#                       "Safari/537.36",
#         "Referer": "https://travel.qunar.com/p-cs300197-shangluo"}
#     # 向网址发送请求
#     with httpx.Client(proxies=proxies,timeout=None) as client:
#         res = client.get(url, headers=headers)
#     html = res.text
#     # 页面html
#     soup = BS(html, 'html.parser')
#     return soup
#
#
# def scenic_spot_all_info():
#     # 清空日志
#     with open('D:\Python\ScenicSpot\scenic_spot.log', 'w') as f:
#         f.truncate(0)
#     try:
#         count = 1
#         scenic_spot_all_info_list = []
#         while True:
#             global toal_pages
#             if count == 1:
#                 url = f'https://travel.qunar.com/p-cs300197-shangluo-jingdian'
#                 soup = request(url)[0]
#                 toal_pages = request(url)[1]
#                 one_page_scenic_spot_info_list = scenic_spot_info(soup)
#                 scenic_spot_all_info_list = scenic_spot_all_info_list + one_page_scenic_spot_info_list
#                 count = count + 1
#                 time.sleep(1)
#             else:
#                 page = f'-1-{count}'
#                 url = f'https://travel.qunar.com/p-cs300197-shangluo-jingdian{page}'
#                 soup = request(url)[0]
#                 toal_pages = request(url)[1]
#                 one_page_scenic_spot_info_list = scenic_spot_info(soup)
#                 scenic_spot_all_info_list = scenic_spot_all_info_list + one_page_scenic_spot_info_list
#                 count = count + 1
#                 time.sleep(1)
#                 # print(count)
#                 if count > int(toal_pages):
#                     if os.path.exists('output.xlsx'):
#                         write_info()
#                         break
#                     else:
#                         # 创建DataFrame，并指定列名
#                         df = pd.DataFrame(scenic_spot_all_info_list,
#                                           columns=['景点名称', '景点评分', '景点攻略数', '景点点评数', '景点驴友到访率',
#                                                    '景点位置', '景点排名', '二级地址', '景点评论'])
#                         # 导出到Excel文件
#                         df.to_excel('output.xlsx', index=False, engine='openpyxl')
#                         write_info()
#                         break
#         return scenic_spot_all_info_list
#     except Exception as e:
#         # 如果在执行过程中发生了任何异常，则捕获它
#         print(f"错误信息: {e}")
#         logger1.error(str(e))
#
#
# def scenic_spot_info(soup):
#     try:
#         # 城市对应页面景区列表
#         scenic_spot_list = []
#         # 筛选中文
#         chinese_pattern = re.compile(r'[\u4e00-\u9fff]+')
#         scenic_spot_label_list = soup.find_all(name="span", attrs={"class": "cn_tit"})
#         cn_titles = [chinese_pattern.findall(str(span))[0] for span in scenic_spot_label_list]
#         print(f'城市对应页面景区列表:{cn_titles}')
#
#         # 景点评分
#         score_list = []  # 景点评分列表
#         score_label_list = soup.find_all(name="span", attrs={"class": 'cur_star'})
#         for score in score_label_list:
#             score_list.append(re.search('\d+', score.get('style')).group())
#
#         print(f'景点评分数列表:{score_list}')
#
#         # 景点攻略
#         strategy_list = []
#         strategy_label_list = soup.find_all(name="div", attrs={"class": 'strategy_sum'})
#         for strategy in strategy_label_list:
#             strategy_list.append(re.search('\d+', str(strategy)).group())
#         print(f'景点攻略数列表：{strategy_list}')
#
#         # 点评
#         remark_on_list = []  # 景点点评数量列表
#         remark_on_label_list = soup.find_all(name='div', attrs={"class": 'comment_sum'})
#         for remark_on in remark_on_label_list:
#             remark_on_list.append(re.search('\d+', str(remark_on)).group())
#         print(f'景点点评数量列表:{remark_on_list}')
#
#         # 排名
#         ranking_list = []
#         ranking_label_list = soup.find_all(name='span', attrs={"class": 'ranking_sum'})
#         for ranking in ranking_label_list:
#             ranking_list.append(ranking.get_text())
#         print(f'景点排名列表:{ranking_list}')
#
#         # 景点驴友到访率
#         vist_list = []
#         visit_label_list = soup.find_all(name='span', attrs={"class": 'comment_sum'})
#         for vist in visit_label_list:
#             vist_list.append(re.search('\d.', str(vist)).group())
#         print(f'景点驴友到访率列表：{vist_list}')
#
#         # 地址列表
#         secondary_label_list = []  # 二级网页链接列表
#         location_list = []  # 地址列表
#
#         location_label_list = soup.find_all(name='a', attrs={"target": '_blank', "class": 'titlink'})
#         for i in location_label_list:
#             secondary_label_list.append(re.search('href="(.*?)"', str(i)).group(1))
#
#         for j in secondary_label_list:
#             soup2 = request2(j)
#             # print(soup2.find_all(name='div', attrs={"class": 'e_summary_list clrfix'})[0])
#             location_list.append(re.search('span>(.*?)<', str(
#                 soup2.find_all(name='div', attrs={"class": 'e_summary_list clrfix'})[0])).group(1))
#         print(f'景点地址列表：{location_list}')
#
#         info = get_comment(soup)
#         # 二级url
#         secondary_url = info[1]
#         # 评论列表
#         all_list = info[0]
#
#         # 单页景点所有信息列表
#         one_page_scenic_spot_info_list = list(
#             zip(cn_titles, score_list, strategy_list, remark_on_list, vist_list, location_list, ranking_list,
#                 secondary_url,
#                 all_list))
#         return one_page_scenic_spot_info_list
#     except Exception as e:
#         # 如果在执行过程中发生了任何异常，则捕获它
#         print(f"错误信息: {e}")
#         logger1.error(str(e))
#
#
# # 写入数据库函数
# def write_info():
#     try:
#         from openpyxl import load_workbook
#         # 加载工作簿
#         workbook = load_workbook('output.xlsx')
#         # 选择工作表
#         sheet = workbook.active
#         # 遍历行和列获取数据
#         data = []
#         for row in sheet.iter_rows(min_row=2, values_only=True):
#             data.append(row)
#         for row in data:
#             # 假设第一列是name，第二列是age
#             ScenicSpot.objects.create(name=row[0],
#                                       rating=int(row[1]),
#                                       strategy_count=int(row[2]),
#                                       review_count=int(row[3]),
#                                       visitor_rate=row[4],
#                                       location=row[5],
#                                       ranking=str(row[6]),
#                                       link_url=row[7],
#                                       comment=(row[8]))
#     except Exception as e:
#         # 如果在执行过程中发生了任何异常，则捕获它
#         print(f"错误信息: {e}")
#         logger1.error(str(e))
#
#
# # 获取评论列表
# def get_comment(soup):
#     try:
#         res = (soup.find_all(name='a', attrs={'class': 'titlink', 'data-beacon': 'poi'}))
#         # 景点二级地址
#         secondary_url = []
#         all_list = []
#         for i in res:
#             secondary_url.append(i['href'])
#         print(secondary_url)
#         for first_url in secondary_url:
#             global list_of_comments
#             # 评论列表
#             list_of_comments = []
#             soup = request2(first_url)
#             time.sleep(1)
#             page_label = soup.find_all('a', attrs={'class': 'page'})
#             # 评论页数总数
#             page_sum = len(page_label)
#             if page_sum != 0:
#                 for count in range(1, page_sum + 1):
#                     second_url = f'{first_url}-1-{count}'
#                     soup = request2(second_url)
#                     time.sleep(1)
#                     comments = soup.find_all(name="div", attrs={"class": "e_comment_content"})
#                     for i in comments:
#                         list_of_comments.append(i.get_text())
#                 all_list.append(list_of_comments)
#             else:
#                 comments = soup.find_all(name="div", attrs={"class": "e_comment_content"})
#                 for i in comments:
#                     list_of_comments.append(i.get_text())
#                 all_list.append(list_of_comments)
#         print(all_list)
#         return all_list, secondary_url
#     except Exception as e:
#         # 如果在执行过程中发生了任何异常，则捕获它
#         print(f"错误信息: {e}")
#         logger1.error(str(e))
#
#
# # print(scenic_spot_all_info())
#
# # ScenicSpot.objects.all().update(comment=None)
