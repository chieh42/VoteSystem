<template>
  <div class="vote-container">
    <div class="role-selector-group">
      <label for="roleSelect">目前身分：</label>
      <select id="roleSelect" v-model="currentRole" class="role-select">
        <option value="user">一般使用者</option>
        <option value="admin">系統管理員</option>
      </select>
    </div>
    <hr />

    <h2>線上投票系統</h2>
    <hr />

    <div v-for="item in voteResults" :key="item.id" class="item-row">
      <label class="checkbox-label">
        <input type="checkbox" :value="item.id" v-model="selected" />
        <span class="item-name">{{ item.name }}</span>
        <span class="item-count">({{ item.vote_count || 0 }} 票)</span>
      </label>

      <div v-if="currentRole === 'admin'" class="action-btn-group">
        <button @click="handleUpdateItem(item.id, item.name)" class="edit-btn">
          修改
        </button>
        <button @click="handleDeleteItem(item.id)" class="delete-btn">
          刪除
        </button>
      </div>
    </div>

    <hr />

    <div class="input-group">
      <label for="voterName">您的姓名：</label>
      <input id="voterName" type="text" v-model="voter" placeholder="請輸入姓名" />
      <span class="hint-text">* 姓名非必填，可直接送出！！</span>
    </div>

    <button @click="submitVote" :disabled="selected.length === 0" class="submit-btn">
      確認送出投票
    </button>

    <template v-if="currentRole === 'admin'">
      <hr />
      <div class="admin-section">
        <div class="input-group">
          <label class="admin-label">新增投票項目：</label>
          <input type="text" v-model="newItemName" placeholder="請輸入新項目名稱（如：鍵盤）" />
        </div>
        <button @click="handleAddItem" :disabled="!newItemName.trim()" class="admin-btn">
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

const voteResults = ref([])
const selected = ref([])
const voter = ref('')
const newItemName = ref('')

// 預設為 user，點擊下拉選單可切換 admin
const currentRole = ref('user')

// 讀取資料
const loadData = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/vote/result')
    voteResults.value = res.data
  } catch (error) {
    console.error('讀取後端資料失敗：', error)
  }
}

// 投票
const submitVote = async () => {
  try {
    for (let id of selected.value) {
      await axios.post('http://localhost:8080/api/vote/vote', null, {
        params: {
          voter: voter.value,
          itemId: id
        }
      })
    }

    alert('恭喜您，投票成功！')
    selected.value = []
    voter.value = ''
    loadData()
  } catch (error) {
    alert('投票失敗，請檢查網路連線')
    console.error(error)
  }
}

// 新增投票項目-管理員
const handleAddItem = async () => {
  try {
    const params = new URLSearchParams()
    params.append('itemName', newItemName.value)
    params.append('voterRole', currentRole.value)

    await axios.post('http://localhost:8080/api/vote/item', params)
    alert('新增成功！')
    newItemName.value = ''
    await loadData()
  } catch (error) {
    console.error('新增項目失敗:', error)
    alert('新增項目失敗，權限不足！')
  }
}

// 修改既有項目名稱-管理員
const handleUpdateItem = async (itemId, oldName) => {
  const newName = prompt(`請輸入投票項目 [${oldName}] 的全新名稱：`, oldName)

  if (newName === null || !newName.trim()) return
  if (newName.trim() === oldName) return

  try {
    const params = new URLSearchParams()
    params.append('itemId', itemId)
    params.append('newName', newName.trim())
    params.append('voterRole', currentRole.value)

    await axios.put('http://localhost:8080/api/vote/item', params)

    alert('已成功更新！')
    await loadData()
  } catch (error) {
    console.error('更新失敗:', error)
    alert('更新失敗，權限不足！')
  }
}

// 刪除投票項目-管理員
const handleDeleteItem = async (itemId) => {
  if (!confirm('確定要刪除此項目嗎？這將會一併清除該項目的所有投票紀錄！')) return

  try {
    await axios.delete(`http://localhost:8080/api/vote/item?itemId=${itemId}&voterRole=${currentRole.value}`)
    alert('項目已成功刪除！')
    await loadData()
  } catch (error) {
    console.error('刪除項目失敗:', error)
    alert('刪除項目失敗，權限不足！')
  }
}

onMounted(loadData)
</script>