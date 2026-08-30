// utils/mockData.ts

import { getSpecialtyAll } from '/@/apis/qlky/tourist';

// 1. 类型定义保持不变
type DistrictDataPoint = { name: string; value: number };
type SpecialtyType = '地理标志产品' | '农产品' | '中药材' | '山珍菌类' | '传统美食' | '手工艺品' | '非遗工艺' | '文创产品';
type SpecialtyItem = { name: string; image: string; origin: string; type: string; link: string; value: number };

// 2. API响应类型
interface ApiSpecialtyItem {
  description: string;
  group_id: string;
  image_url: string;
  origin_place: string;
  product_name: string;
  product_type: string;
}

// 3. 智能转换器
const transformApiData = {
  // 转换单个特产
  toSpecialtyItem(apiItem: ApiSpecialtyItem): SpecialtyItem {
    return {
      name: apiItem.product_name,
      image: apiItem.image_url,
      origin: transformApiData.extractCounty(apiItem.origin_place),
      type: apiItem.product_type,
      link: '#',
      value: transformApiData.calculateHotValue(apiItem)
    };
  },

  // 提取县级地名
  extractCounty(origin: string): string {
    const parts = origin.split(/[，,]/);
    return parts.length >= 2 ? parts[1].replace('县', '') + '县' : '未知产地';
  },

  // 类型标准化
  normalizeType(rawType: string): SpecialtyType {
    const typeMap: Record<string, SpecialtyType> = {
      '地理标志': '地理标志产品',
      '干货': '农产品',
      '药材': '中药材',
      '菌类': '山珍菌类',
      '传统': '传统美食',
      '手工艺': '手工艺品',
      '非遗': '非遗工艺',
      '文创': '文创产品'
    };
    return typeMap[rawType] || '农产品';
  },

  // 热度计算
  calculateHotValue(item: ApiSpecialtyItem): number {
    const descWeight = Math.floor(item.description.length / 10); // 每10字计1分
    const imgWeight = item.image_url.includes('zhtechan.cn') ? 50 : 30; // 主站图片加分
    return descWeight + imgWeight + Math.floor(Math.random() * 20);
  },

  // 生成城市数据
  generateCityData(items: any[]) {
    const cityMap = new Map<string, number>();
    items.forEach(item => {
      const city = item.origin.replace(/县$/, '');
      const count = cityMap.get(city) || 0;
      cityMap.set(city, count + 1);
    });

    return Array.from(cityMap.entries()).map(([name, value]) => ({
      name,
      value: value * 100 // 转换为模拟数值
    }));
  },

  // 生成品类比例
  generateCategoryProportion(items: ApiSpecialtyItem[]) {
    const typeMap = new Map<string, number>();
    
    items.forEach(item => {
      const count = typeMap.get(item.product_type) || 0;
      typeMap.set(item.product_type, count + 1);
    });

    return Array.from(typeMap.entries()).map(([type, count]) => [type, count]).slice(0, 5) // 取前五名;
  }
};

const staticSpecialties: Record<string, SpecialtyItem[]> = {
	shangluo: [
		{ name: '柞水木耳（地理标志）', image: 'https://example.com/zhashui_muer.jpg', origin: '柞水县', type: '地理标志产品', link: '#', value: 9200 },
		{ name: '丹凤葡萄酒（地理标志）', image: 'https://example.com/danfeng_wine.jpg', origin: '丹凤县', type: '地理标志产品', link: '#', value: 8600 },
		{ name: '商南茶', image: 'https://example.com/shangnan_tea.jpg', origin: '商南县', type: '地理标志产品', link: '#', value: 7800 },
		{ name: '镇安板栗', image: 'https://example.com/zhengan_chestnut.jpg', origin: '镇安县', type: '地理标志产品', link: '#', value: 8200 },
		{ name: '山阳九眼莲', image: 'https://example.com/shanyang_lotus.jpg', origin: '山阳县', type: '农产品', link: '#', value: 6800 },
		{ name: '商洛香菇', image: 'https://example.com/shangluo_mushroom.jpg', origin: '商州区', type: '山珍菌类', link: '#', value: 7300 },
		{ name: '洛南豆腐干', image: 'https://example.com/luonan_tofu.jpg', origin: '洛南县', type: '传统美食', link: '#', value: 6500 },
		{ name: '镇安腊肉', image: 'https://example.com/zhengan_bacon.jpg', origin: '镇安县', type: '传统美食', link: '#', value: 7100 },
		{ name: '柞水洋芋糍粑', image: 'https://example.com/zhashui_potato_cake.jpg', origin: '柞水县', type: '传统美食', link: '#', value: 6300 },
		{ name: '商南猕猴桃', image: 'https://example.com/shangnan_kiwi.jpg', origin: '商南县', type: '农产品', link: '#', value: 6900 },
		{ name: '丹凤天麻', image: 'https://example.com/danfeng_gastrodia.jpg', origin: '丹凤县', type: '中药材', link: '#', value: 7500 },
		{ name: '山阳核桃', image: 'https://example.com/shanyang_walnut.jpg', origin: '山阳县', type: '农产品', link: '#', value: 6700 },
		{ name: '镇安魔芋', image: 'https://example.com/zhengan_konjac.jpg', origin: '镇安县', type: '农产品', link: '#', value: 6100 },
		{ name: '商州竹编', image: 'https://example.com/shangzhou_bamboo.jpg', origin: '商州区', type: '手工艺品', link: '#', value: 5900 },
		{ name: '洛南剪纸', image: 'https://example.com/luonan_papercut.jpg', origin: '洛南县', type: '手工艺品', link: '#', value: 5700 },
		{ name: '柞水木雕', image: 'https://example.com/zhashui_woodcarving.jpg', origin: '柞水县', type: '手工艺品', link: '#', value: 6200 },
		{ name: '商南漆器', image: 'https://example.com/shangnan_lacquer.jpg', origin: '商南县', type: '手工艺品', link: '#', value: 6400 },
		{ name: '镇安石砚', image: 'https://example.com/zhengan_inkstone.jpg', origin: '镇安县', type: '手工艺品', link: '#', value: 6600 },
		{ name: '丹凤皮影', image: 'https://example.com/danfeng_shadowplay.jpg', origin: '丹凤县', type: '非遗工艺', link: '#', value: 7100 },
		{ name: '山阳民歌CD', image: 'https://example.com/shanyang_folk.jpg', origin: '山阳县', type: '文创产品', link: '#', value: 5300 },
	],
};

// utils/mockData.ts

// API响应类型
interface ApiSpecialtyItem {
	description: string;
	group_id: string;
	image_url: string;
	origin_place: string;
	product_name: string;
	product_type: string;
}

const cityConfig: Record<
	string,
	{
		name: string;
		districts: string[];
		dataPoints: DistrictDataPoint[];
		sum: number;
	}
> = {
	shangluo: {
		name: '商洛市',
		districts: ['商州区', '柞水县', '山阳县', '洛南县', '丹凤县', '商南县', '镇安县'],
		dataPoints: [
			{ name: '商州区', value: 85 },
			{ name: '柞水县', value: 120 },
			{ name: '山阳县', value: 95 },
			{ name: '洛南县', value: 78 },
			{ name: '丹凤县', value: 110 },
			{ name: '商南县', value: 92 },
			{ name: '镇安县', value: 105 },
		],
		sum: 685, // 各value总和
	},
	hanzhong: {
		name: '汉中市',
		districts: ['汉台区', '勉县', '略阳县', '南郑区', '城固县', '洋县', '佛坪县'],
		dataPoints: [
			{ name: '汉台区', value: 150 },
			{ name: '勉县', value: 130 },
			{ name: '略阳县', value: 90 },
			{ name: '南郑区', value: 115 },
			{ name: '城固县', value: 125 },
			{ name: '洋县', value: 100 },
			{ name: '佛坪县', value: 80 },
		],
		sum: 790,
	},
};
const staticProportions: Record<string, Array<[string, number]>> = {
	shangluo: [
		['柞水地理标志', 35],
		['山阳中药材', 28],
		['商州山珍菌类', 20],
		['镇安农副产品', 17],
	],
	xian: [
		['临潼地理标志', 40],
		['周至传统美食', 30],
		['长安文创产品', 25],
		['蓝田非遗工艺', 15],
	],
};

// 5. 核心函数实现
export const generateMockMapData =  async (city: string) => {
  try {
    const apiData = await getSpecialtyAll() as any; // 实际使用需await
    if(apiData.code === 8888){
      const cityData = transformApiData.generateCityData(apiData.data.records);
      return {
        ...cityConfig[city],
        dataPoints: cityData,
        sum: cityData.reduce((acc, cur) => acc + cur.value, 0)
      };
    }
    return cityConfig[city] || cityConfig.shangluo;
  } catch {
    return cityConfig[city] || cityConfig.shangluo;
  }
};

export async function generateSpecialties(cityKey: string) {
	const res = await getSpecialtyAll() as any;
    return res.data.records
      .map(transformApiData.toSpecialtyItem)
      .sort((a:any, b:any) => b.value - a.value);
}

export const generateProportionView = async (cityCode: string) => {
	const apiData = await getSpecialtyAll() as any; // 实际使用需await
    return transformApiData.generateCategoryProportion(apiData.data.records);
};