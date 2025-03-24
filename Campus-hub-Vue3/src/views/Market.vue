<template>
  <div class="market">
    <div class="market-header">
      <h1>校园二手市场</h1>
      <div class="market-actions">
        <el-input
          v-model="searchText"
          placeholder="搜索商品..."
          prefix-icon="Search"
          class="search-input"
        />
        <el-button type="primary" @click="showPublishDialog">
          发布商品
        </el-button>
      </div>
    </div>

    <div class="category-filter">
      <el-radio-group v-model="selectedCategory">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button v-for="category in categories" :key="category.id" :label="category.id">
          {{ category.name }}
        </el-radio-button>
      </el-radio-group>
    </div>

    <div class="products-grid">
      <el-card v-for="product in filteredProducts" :key="product.id" class="product-card">
        <img :src="product.image" class="product-image" />
        <h3>{{ product.title }}</h3>
        <p class="price">¥{{ product.price }}</p>
        <p class="description">{{ product.description }}</p>
        <div class="product-footer">
          <span class="seller">{{ product.seller }}</span>
          <el-button type="primary" link @click="contactSeller(product)">
            联系卖家
          </el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

const searchText = ref('')
const selectedCategory = ref('')

const categories = [
  { id: 'books', name: '教材书籍' },
  { id: 'electronics', name: '电子产品' },
  { id: 'daily', name: '生活用品' },
  { id: 'sports', name: '运动器材' },
  { id: 'others', name: '其他' }
]

const products = ref([
  {
    id: 1,
    title: '高等数学教材',
    price: 20,
    description: '九成新，配套习题册',
    image: '/src/assets/images/book.jpg',
    category: 'books',
    seller: '张同学'
  },
  {
    id: 2,
    title: '蓝牙耳机',
    price: 100,
    description: '使用3个月，音质好',
    image: '/src/assets/images/headphone.jpg',
    category: 'electronics',
    seller: '李同学'
  },
  // 更多商品数据...
])

const filteredProducts = computed(() => {
  return products.value.filter(product => {
    const matchCategory = !selectedCategory.value || product.category === selectedCategory.value
    const matchSearch = !searchText.value || 
      product.title.toLowerCase().includes(searchText.value.toLowerCase()) ||
      product.description.toLowerCase().includes(searchText.value.toLowerCase())
    return matchCategory && matchSearch
  })
})

const showPublishDialog = () => {
  // TODO: 实现发布商品弹窗
}

const contactSeller = (product) => {
  // TODO: 实现联系卖家功能
}
</script>

<style scoped>
.market {
  padding: 2rem;
}

.market-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.market-actions {
  display: flex;
  gap: 1rem;
}

.search-input {
  width: 300px;
}

.category-filter {
  margin-bottom: 2rem;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 2rem;
}

.product-card {
  transition: transform 0.3s ease;
}

.product-card:hover {
  transform: translateY(-5px);
}

.product-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 4px;
}

.price {
  color: #f56c6c;
  font-size: 1.2rem;
  font-weight: bold;
  margin: 0.5rem 0;
}

.description {
  color: #666;
  margin-bottom: 1rem;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.seller {
  color: #909399;
  font-size: 0.9rem;
}
</style> 