// src/composables/useTourismData.ts
import { ref } from 'vue'
export function useTourismData() {
  const spots = ref<any[]>([])
  const currentSpot = ref<any | null>(null)
  const loading = ref(false)
  const error = ref<Error | null>(null)

  const loadDemoData = () => {
    return [
      {
        id: 1,
        name: '西湖风景区',
        rating: 4.8,
        strategyCount: 1285,
        reviewCount: 25689,
        visitorRate: 0.92,
        ranking: "杭州康养项目第1名",
        secondaryAddress: "浙江省杭州市西湖区龙井路1号",
        latitude: 30.246026,
        longitude: 120.137847,
        openTime: "全天开放",
        location: "杭州市区西部",
        ticketPrice: "免费",
        bestSeason: "春秋季",
        imageUrl: "https://example.com/west-lake.jpg"
      }
    ]
  }

  const fetchSpots = async () => {
    try {
      loading.value = true
      // 实际项目替换为API调用
        
    } catch (err) {
      error.value = err as Error
    } finally {
      loading.value = false
    }
  }

  return {
    spots,
    currentSpot,
    loading,
    error,
    fetchSpots
  }
}