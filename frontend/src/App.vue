<template>
  <div class="vote-container">
    <h2>線上投票系統</h2>
    <hr />

    <div class="auth-section">
      <div v-if="!isLoggedIn">
        <h3>會員登入</h3>

        <div class="auth-form-group">
          <label>帳號：</label>
          <input type="text" v-model="authForm.username" placeholder="請輸入帳號" />
        </div>

        <div class="auth-form-group">
          <label>密碼：</label>
          <input type="password" v-model="authForm.password" placeholder="請輸入密碼" />
        </div>

        <div class="auth-btn-group">
          <button @click="handleLogin">登入</button>
          <button @click="handleRegister" style="background: #666; border-color: #666;">註冊</button>
        </div>

        <div class="demo-account-hint">
          <strong>測試專用（Demo Accounts）：</strong><br />
          一般使用者：<code>user1</code> / 密碼 <code>user123</code><br />
          系統管理員：<code>admin</code> / 密碼 <code>admin123</code>
        </div>
      </div>

      <div v-else class="login-status-box">
        <div>
          使用者：<strong>{{ currentUsername }}</strong>
          <span> | </span>
          權限：<span :style="{ color: currentUserRole === 'admin' ? '#e67e22' : '#111', fontWeight: 'bold' }">{{ currentUserRole.toUpperCase() }}</span>
        </div>
        <button @click="handleLogout" class="logout-btn">安全登出</button>
      </div>
    </div>
    <hr />

    <div v-for="item in voteResults" :key="item.id" class="item-row">
      <label class="checkbox-label">
        <input type="checkbox" :value="item.id" v-model="selected" :disabled="!isLoggedIn" />
        <span class="item-name">{{ item.name }}</span>
        <span class="item-count">({{ item.vote_count || 0 }} 票)</span>
      </label>

      <button
        v-if="currentUserRole === 'admin'"
        @click="handleUpdateItem(item.id, item.name)"
        class="edit-btn"
      >
        修改名稱
      </button>
    </div>

    <hr />

    <div v-if="!isLoggedIn" class="hint-text" style="color: #ff2828; font-weight: bold; margin-bottom: 12px;">
      * 先登入系統，才可以解鎖投票功能呦。
    </div>

    <button @click="submitVote" :disabled="selected.length === 0 || !isLoggedIn" class="submit-btn">
      確認送出投票
    </button>

    <hr />

    <div v-if="currentUserRole === 'admin'" class="admin-section">
      <div class="input-group">
        <label>新增投票項目：</label>
        <input type="text" v-model="newItemName" placeholder="請輸入新項目名稱（如：鍵盤）" />
      </div>
      <button @click="handleAddItem" :disabled="!newItemName.trim()">
        確認新增項目
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import './vote.css'

// 宣告所有響應式變數
const voteResults = ref([])
const selected = ref([])
const newItemName = ref('')
const authForm = ref({ username: '', password: '' })

const currentUsername = ref(localStorage.getItem('username') || '')
const currentUserRole = ref(localStorage.getItem('user_role') || '')
const jwtToken = ref(localStorage.getItem('jwt_token') || '')

const isLoggedIn = computed(() => !!jwtToken.value)

// 設定 Axios 攔截器（jwtToken.value）
axios.interceptors.request.use(config => {
  if (jwtToken.value) {
    config.headers.Authorization = `Bearer ${jwtToken.value}`
  }
  return config;
}, error => {
  return Promise.reject(error);
});

// 會員註冊
const handleRegister = async () => {
  if (!authForm.value.username.trim() || !authForm.value.password.trim()) {
    alert('請輸入完整的帳號與密碼！')
    return
  }
  try {
    const res = await axios.post('http://localhost:8080/api/auth/register', {
      username: authForm.value.username,
      password: authForm.value.password
    })
    alert(res.data.message || '註冊成功，請執行登入！')
  } catch (error) {
    alert(error.response?.data || '註冊失敗')
  }
}

// 會員登入
const handleLogin = async () => {
  if (!authForm.value.username.trim() || !authForm.value.password.trim()) {
    alert('請輸入帳號與密碼！')
    return
  }
  try {
    const res = await axios.post('http://localhost:8080/api/auth/login', {
      username: authForm.value.username,
      password: authForm.value.password
    })

    const role = res.data.role

    localStorage.setItem('jwt_token', res.data.token)
    localStorage.setItem('username', authForm.value.username)
    localStorage.setItem('user_role', role)

    jwtToken.value = res.data.token
    currentUsername.value = authForm.value.username
    currentUserRole.value = role

    authForm.value.username = ''
    authForm.value.password = ''
    loadData()
  } catch (error) {
    alert(error.response?.data || '登入失敗，請檢查帳號或密碼')
  }
}

// 會員登出
const handleLogout = () => {
  localStorage.clear()
  jwtToken.value = ''
  currentUsername.value = ''
  currentUserRole.value = ''
  selected.value = []
}

// 讀取資料（已精簡：攔截器會自動帶 Token）
const loadData = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/vote/result')
    voteResults.value = res.data
  } catch (error) {
    console.error('讀取失敗：', error)
  }
}

// 送出投票（攔截器會帶 Token）
const submitVote = async () => {
  try {
    for (let id of selected.value) {
      await axios.post('http://localhost:8080/api/vote/vote', null, {
        params: { itemId: id }
      })
    }
    alert('恭喜您，投票成功！')
    selected.value = []
    loadData()
  } catch (error) {
    alert(error.response?.data || '投票失敗')
  }
}

// 新增投票項目-管理員
const handleAddItem = async () => {
  try {
    await axios.post('http://localhost:8080/api/vote/items/add', null, {
      params: { itemName: newItemName.value }
    })
    alert('新增成功！')
    newItemName.value = ''
    await loadData()
  } catch (error) {
    if (error.response?.status === 403) {
      alert('權限不足或項目名稱重複！')
    } else {
      alert('新增項目失敗')
    }
  }
}

// 修改既有項目名稱-管理員
const handleUpdateItem = async (itemId, oldName) => {
  const newName = prompt(`請輸入投票項目 [${oldName}] 的全新名稱：`, oldName)
  if (newName === null || !newName.trim()) return
  if (newName.trim() === oldName) return

  try {
    await axios.put('http://localhost:8080/api/vote/items/update', null, {
      params: {
        itemId: itemId,
        newName: newName.trim()
      }
    })
    alert('已成功更新！')
    await loadData()
  } catch (error) {
    if (error.response?.status === 403) {
      alert('權限不足或項目名稱重複！')
    } else {
      alert('更新失敗！')
    }
  }
}

onMounted(loadData)
</script>