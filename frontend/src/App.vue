<template>
  <div class="vote-container">
    <h2>線上投票系統</h2>
    <hr />

    <div v-for="item in voteResults" :key="item.id" class="item-row">
      <label class="checkbox-label">
        <input type="checkbox" :value="item.id" v-model="selected" />
        <span class="item-name">{{ item.name }}</span>
        <span class="item-count">({{ item.vote_count || 0 }} 票)</span>
      </label>

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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import './vote.css'

const voteResults = ref([])
const selected = ref([])
const voter = ref('')
const newItemName = ref('');

// 資料
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

    // 清空狀態並重新刷票數
    selected.value = []
    loadData()
  } catch (error) {
    alert('投票失敗，請檢查網路連線')
    console.error(error)
  }
}

// 新增投票項目
const handleAddItem = async () => {
  try {
    const params = new URLSearchParams();
    params.append('itemName', newItemName.value);

    await axios.post('http://localhost:8080/api/vote/item', params);
    alert('新增成功！');
    newItemName.value = '';
    await loadData();           // 重新撈取後端票數
  } catch (error) {
    console.error('新增項目失敗:', error);
    alert('新增項目失敗，請檢查後端連線');
  }
};

/* 刪除投票項目
const handleDeleteItem = async (itemId) => {
  if (!confirm('⚠️ 確定要刪除此項目嗎？這將會一併清除該項目的所有投票紀錄！')) return;

  try {
    await axios.delete(`http://localhost:8080/api/vote/item?itemId=${itemId}`);
    alert('項目已成功刪除！');
    await loadData();
  } catch (error) {
    console.error('刪除項目失敗:', error);
    alert('刪除項目失敗');
  }
};
*/
onMounted(loadData)
</script>