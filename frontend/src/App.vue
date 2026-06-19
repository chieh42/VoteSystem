<template>
  <div class="vote-container">
    <h2>線上投票系統</h2>
    <hr />
    <div class="role-selector-group">
      <label for="roleSelect">目前身分：</label>
      <select id="roleSelect" v-model="currentRole" class="role-select">
        <option value="user">一般使用者</option>
        <option value="admin">系統管理員</option>
      </select>
    </div>
    <hr />

    <div v-for="item in voteResults" :key="item.id" class="item-row">
      <label class="checkbox-label">
        <input type="checkbox" :value="item.id" v-model="selected" :disabled="isSubmitting" />
        <span class="item-name">{{ item.name }}</span>
        <span class="item-count">({{ item.vote_count || 0 }} 票)</span>
      </label>

      <div v-if="currentRole === 'admin'" class="action-btn-group">
        <button @click="handleUpdateItem(item.id, item.name)" class="edit-btn" :disabled="isSubmitting">
          修改
        </button>
        <button @click="handleDeleteItem(item.id)" class="delete-btn" :disabled="isSubmitting">
          刪除
        </button>
      </div>
    </div>

    <hr />

    <div class="input-group">
      <label for="voterName">您的姓名：</label>
      <input id="voterName" type="text" v-model="voter" placeholder="請輸入姓名" :disabled="isSubmitting" />
      <span class="hint-text">* 姓名非必填，可直接送出！！</span>
    </div>

    <button @click="submitVote" :disabled="selected.length === 0 || isSubmitting" class="submit-btn">
      {{ isSubmitting ? '投票傳送中...' : '確認送出投票' }}
    </button>

    <template v-if="currentRole === 'admin'">
      <hr />
      <div class="admin-section">
        <div class="input-group">
          <label class="admin-label">新增投票項目：</label>
          <input type="text" v-model="newItemName" placeholder="請輸入新項目名稱（如：滑鼠）" :disabled="isSubmitting" />
        </div>
        <button @click="handleAddItem" :disabled="!newItemName.trim() || isSubmitting" class="admin-btn">
          確認新增項目
        </button>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import './vote.css'

const api = axios.create({
  baseURL: 'http://localhost:8080/api/vote'
})

const voteResults = ref([])
const selected = ref([])
const voter = ref('')
const newItemName = ref('')
const currentRole = ref('user')

const isSubmitting = ref(false)

// 讀取資料
const loadData = async () => {
  try {
    const res = await api.get('/result')
    voteResults.value = res.data
  } catch (error) {
    console.error('讀取後端資料失敗：', error)
  }
}

// 投票
const submitVote = async () => {
  isSubmitting.value = true
  try {
    const requests = selected.value.map(id =>
      api.post('/vote', null, {
        params: {
          voter: voter.value.trim(),
          itemId: id
        }
      })
    )

    await Promise.all(requests)

    alert('恭喜您，投票成功！')
    selected.value = []
    voter.value = ''
    await loadData()
  } catch (error) {
    alert('投票失敗，請檢查網路連線')
    console.error(error)
  } finally {
    isSubmitting.value = false
  }
}

// 新增投票項目
const handleAddItem = async () => {
  isSubmitting.value = true
  try {
    const params = new URLSearchParams()
    params.append('itemName', newItemName.value.trim())
    params.append('voterRole', currentRole.value)

    await api.post('/item', params)
    alert('新增成功！')
    newItemName.value = ''
    await loadData()
  } catch (error) {
    console.error('新增項目失敗:', error)
    alert('新增項目失敗，權限不足！')
  } finally {
    isSubmitting.value = false
  }
}

// 修改既有項目名稱
const handleUpdateItem = async (itemId, oldName) => {
  const newName = prompt(`請輸入投票項目 [${oldName}] 的全新名稱：`, oldName)

  if (newName === null || !newName.trim()) return
  if (newName.trim() === oldName) return

  isSubmitting.value = true
  try {
    const params = new URLSearchParams()
    params.append('itemId', itemId)
    params.append('newName', newName.trim())
    params.append('voterRole', currentRole.value)

    await api.put('/item', params)

    alert('已成功更新！')
    await loadData()
  } catch (error) {
    console.error('更新失敗:', error)
    alert('更新失敗，權限不足！')
  } finally {
    isSubmitting.value = false
  }
}

// 刪除投票項目
const handleDeleteItem = async (itemId) => {
  if (!confirm('確定要刪除此項目嗎？這將會一併清除該項目的所有投票紀錄！')) return

  isSubmitting.value = true
  try {
    await api.delete(`/item`, {
      params: { itemId, voterRole: currentRole.value }
    })
    alert('項目已成功刪除！')
    await loadData()
  } catch (error) {
    console.error('刪除項目失敗:', error)
    alert('刪除項目失敗，權限不足！')
  } finally {
    isSubmitting.value = false
  }
}

onMounted(loadData)
</script>