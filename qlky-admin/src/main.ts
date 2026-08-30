import { createApp } from 'vue';
import pinia from '/@/stores/index';
import App from '/@/App.vue';
import router from '/@/router';
import { directive } from '/@/directive/index';
import { i18n } from '/@/i18n/index';
import other from '/@/utils/other';
import '@kjgl77/datav-vue3/dist/style.css'
import ElementPlus from 'element-plus';
import '/@/theme/index.scss';
import VueGridLayout from 'vue-grid-layout';
import DataVVue3 from '@kjgl77/datav-vue3'
const app = createApp(App);
directive(app);
other.elSvg(app);

app.use(pinia).use(router).use(ElementPlus).use(i18n).use(DataVVue3).use(VueGridLayout).mount('#app');
