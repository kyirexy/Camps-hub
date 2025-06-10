<template>
  <div class="market">
    <div class="market-header">
      <div class="header-content">
        <h1 class="title">校园二手市场</h1>
        <p class="subtitle">发现优质二手好物，让闲置物品流转起来</p>
        <div class="actions">
          <button class="action-btn all">全部</button>
          <div class="search-box">
            <input type="text" placeholder="搜索商品..." />
            <i class="search-icon">🔍</i>
          </div>
          <button class="action-btn publish">发布商品</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.market {
  min-height: 100vh;
  background-color: #f8fafc;
}

.market-header {
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  padding: 3rem 2rem;
  color: white;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
}

.title {
  font-size: 3rem;
  font-weight: 800;
  margin: 0;
  background: linear-gradient(to right, #ffffff, #e2e8f0);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.subtitle {
  font-size: 1.25rem;
  opacity: 0.9;
  margin: 1rem 0 2rem;
}

.actions {
  display: flex;
  gap: 1.25rem;
  align-items: center;
}

.action-btn {
  padding: 0.875rem 1.75rem;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.action-btn.all {
  background-color: rgba(255, 255, 255, 0.15);
  color: white;
  backdrop-filter: blur(8px);
}

.action-btn.all:hover {
  background-color: rgba(255, 255, 255, 0.25);
  transform: translateY(-1px);
}

.action-btn.publish {
  background-color: #f43f5e;
  color: white;
}

.action-btn.publish:hover {
  background-color: #e11d48;
  transform: translateY(-1px);
}

.search-box {
  flex: 1;
  position: relative;
  max-width: 600px;
}

.search-box input {
  width: 100%;
  padding: 1rem 1.25rem 1rem 3rem;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  background-color: rgba(255, 255, 255, 0.15);
  color: white;
  transition: all 0.2s ease;
  backdrop-filter: blur(8px);
}

.search-box input::placeholder {
  color: rgba(255, 255, 255, 0.7);
}

.search-box input:focus {
  outline: none;
  background-color: rgba(255, 255, 255, 0.25);
  box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.2);
}

.search-icon {
  position: absolute;
  left: 1rem;
  top: 50%;
  transform: translateY(-50%);
  color: rgba(255, 255, 255, 0.7);
  font-style: normal;
  font-size: 1.25rem;
}

@media (max-width: 768px) {
  .market-header {
    padding: 2rem 1.5rem;
  }

  .title {
    font-size: 2.25rem;
  }

  .actions {
    flex-direction: column;
    gap: 1rem;
  }

  .search-box {
    width: 100%;
    max-width: none;
  }

  .action-btn {
    width: 100%;
  }
}
</style> 