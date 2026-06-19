<template>
  <div class="vote-container">
    <h2>線上投票系統</h2>
    <hr />

    <div class="vote-items">
      <h3>現有投票項目</h3>
      <div v-for="item in items" :key="item.id" class="item-row">
        <label class="checkbox-label">
          <input type="checkbox" v-model="selected" :value="item.id" />
          <span class="item-name">{{ item.name }}</span>
          <span class="item-count">({{ item.vote_count }} 票)</span>
        </label>
      </div>
    </div>

    <hr />

    <div class="voter-form">
      <h3>進行投票</h3>
      <div class="input-group">
        <label for="voterName">您的姓名：</label>
        <input id="voterName" v-model="voter" placeholder="請輸入姓名" />
      </div>

      <button @click="submitVote" :disabled="!voter || selected.length === 0">
        確認送出投票
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import './vote.css'

const items = ref([])
const selected = ref([])
const voter = ref('')

// 資料
const loadData = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/vote/result')
    items.value = res.data
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

    // 清空狀態並重新刷票數
    selected.value = []
    loadData()
  } catch (error) {
    alert('投票失敗，請檢查網路連線')
    console.error(error)
  }
}

onMounted(loadData)
</script>
