import axios from 'axios';

const observationPage = '/weather-page-proxy/weather/101110601.shtml';
const forecastPage = '/weather-proxy/weather_index/101110601.html';

type Observation = {
	od21: string;
	od22: string;
	od24: string;
	od25: string;
	od26: string;
	od27: string;
};

type Forecast = {
	fa: string;
	fb: string;
	fc: string;
	fd: string;
	fe: string;
	fg: string;
	fj: string;
};

const weatherNames: Record<string, string> = {
	'00': '晴',
	'01': '多云',
	'02': '阴',
	'03': '阵雨',
	'04': '雷阵雨',
	'07': '小雨',
	'08': '中雨',
	'09': '大雨',
	'10': '暴雨',
	'13': '阵雪',
	'14': '小雪',
	'15': '中雪',
	'16': '大雪',
};

function readVariable<T>(source: string, variableName: string): T {
	const declaration = source.match(new RegExp(`var\\s+${variableName}\\s*=\\s*`));
	if (!declaration || declaration.index === undefined) throw new Error(`Weather response did not contain ${variableName}`);
	const start = declaration.index + declaration[0].length;
	let depth = 0;
	let quoted = false;
	let escaped = false;
	for (let index = start; index < source.length; index += 1) {
		const char = source[index];
		if (quoted) {
			escaped = !escaped && char === '\\';
			if (char === '"' && !escaped) quoted = false;
			continue;
		}
		if (char === '"') {
			quoted = true;
			continue;
		}
		if (char === '{') depth += 1;
		if (char === '}') {
			depth -= 1;
			if (depth === 0) return JSON.parse(source.slice(start, index + 1)) as T;
		}
	}
	throw new Error(`Weather response contained incomplete ${variableName}`);
}

export async function getLiveWeather() {
	const [observationResponse, forecastResponse] = await Promise.all([
		axios.get(observationPage, { timeout: 10000 }),
		axios.get(forecastPage, { timeout: 10000 }),
	]);
	const observed = readVariable<{ od: { od2: Observation[] } }>(observationResponse.data, 'observe24h_data').od.od2;
	const forecast = readVariable<{ f: Forecast[] }>(forecastResponse.data, 'fc').f;

	return {
		observed: observed
			.slice()
			.reverse()
			.map((item) => ({
				time: `${item.od21}:00`,
				temperature: Number(item.od22),
				windDirection: item.od24 || '无持续风向',
				windPower: Number(item.od25 || 0),
				precipitation: Number(item.od26 || 0),
				humidity: Number(item.od27 || 0),
			})),
		forecast: forecast.map((item) => ({
			date: item.fj,
			weather: weatherNames[item.fa] || '未知',
			tempHigh: Number(item.fc),
			tempLow: Number(item.fd),
			windDirection: item.fe || '无持续风向',
			windPower: item.fg || '微风',
		})),
	};
}
