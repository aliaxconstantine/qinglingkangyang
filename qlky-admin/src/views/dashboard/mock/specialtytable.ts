import { getSpecialtyAll } from '/@/apis/qlky/tourist';

export const generateSpecTable = async () => {
	const res = (await getSpecialtyAll()) as any;
	return res.data.records;
};
