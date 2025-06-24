<script setup>

// ===================导入区===================
import axios from "axios";
import {onMounted,ref, toRefs,reactive} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";


const open = ref(false);
const title = ref("");

const data = reactive({
  userform: {
    id: null,
    username: null,
    email: null,
    password: null,
    nickname: null,
    role: null,
    status: null
  },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    username: null,
    email: null,
    password: null,
    nickname: null,
    role: null,
    status: null,
  },
  rules: {
  }
});

// 表单重置
function reset() {
  userform.value = {
    id: null,
    username: null,
    email: null,
    password: null,
    nickname: null,
    role: null,
    status: null
  };
}

const { queryParams, userform, rules } = toRefs(data);


/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加用户";
}

// 取消按钮
function cancel() {
  open.value = false;
  reset();
}

// ===============功能区===============
const addUserSave = () => {
  console.log(userform.value);
  // 使用 axios 给后端发送请求
  axios({
    url: "/api/users/doUserAddSave",
    method: "post",
    data: userform.value
  }).then((response)=>{
    if (response.data.processResult == "SUCCESS") {
      ElMessage({
        "message": "保存成功！",
        "type": "success"
      });
      open.value = false;
      loadUserlist();
    } else {
      ElMessage.error({
        "message": response.data.failedMessage
      })
    }
  });
}

const updateUserSave = () => {
  console.log(userform.value);
  // 使用 axios 给后端发送请求
  axios({
    url: "/api/users/doUserEditSave",
    method: "put",
    data: userform.value
  }).then((response)=>{
    if (response.data.processResult == "SUCCESS") {
      ElMessage({
        "message": "保存成功！",
        "type": "success"
      });
      open.value = false;
      loadUserlist();
    } else {
      ElMessage.error({
        "message": response.data.failedMessage
      })
    }
  });
}

/** 提交按钮 */
function submitForm() {
  if (userform.value.id != null) {
    updateUserSave();
  } else {
    addUserSave();
  }
}


// 给更新的按钮绑定单击响应函数
const doEdit = (currentLineData)=>{
  reset();
  const _id = currentLineData.id
  // 给后端发送请求，根据 id 查询 User 对象用于回显、渲染表单
  axios({
    url: "/api/users/doFindUserById/" + _id,
    method: "get"
  }).then((response)=>{
    if (response.data.processResult == "SUCCESS") {
      console.log(response.data.queryResultData)
      userform.value = response.data.queryResultData;
      open.value = true;
      title.value = "修改用户";
    } else {
      ElMessage.error({"message":response.data.failedMessage})
    }
  });
}

// 给删除按钮绑定单击响应函数
const doRemove = (currentLineData) => {
  // 1、先弹出确认框，向用户确认是否真的删除
  ElMessageBox.confirm(
      `您真的要删除${currentLineData.username}这条记录吗？`,
      "用户系统管理系统提示",
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消删除',
        type: 'danger',
      }
  ).then(()=>{
    // 2、用户点击了确定，所以这里执行删除
    let axiosObj = axios({
      "url": "/api/users/doUserRemove/" + currentLineData.id,
      "method": "delete"
    }).then((response)=>{
      if (response.data.processResult == "SUCCESS") {
        ElMessage({
          "message": "恭喜您！删除成功！",
          "type": "success"
        })

        // 重新加载列表数据
        loadUserlist();
      } else {
        ElMessage.error({message: response.data.failedMessage});
      }
    });

    console.log(axiosObj);

  }).catch(()=>{
    // 3、用户点击了取消，所以这里弹出一个相关提示即可
    ElMessage({
      "message": "那您再想想~"
    })
  });
}



// ===================功能区===================
// 对接 Vue 生命周期的 mounted 环节，在组件加载完成后加载数据列表
onMounted(() => {
  // 调用已封装好的方法加载列表数据
  loadUserlist();
});
// 封装响应式数据，被包装的数据是一个数组
let userlist = ref([]);
// 声明一个专门的方法，用来向服务器索取列表数据
let loadUserlist = ()=>{
  // 发送 Ajax 请求
  axios({
    "url":"/api/users/doUserList"
  }).then((response)=>{
    // 我们要求后端所有返回的数据都符合 Result 的数据结构
    // response.data 是响应体，对应 Result 的数据结构
    // response.data.processResult 就是成功或失败
    if (response.data.processResult == "SUCCESS") {
      // 弹出友好提示：加载数据成功
      ElMessage({
        message: '加载列表成功！',
        type: 'success',
      });
      // response.data.queryResultData 就是查询结果数据
      userlist.value = response.data.queryResultData;
    }

    if (response.data.processResult == "FAILED") {
      // 弹出友好提示：加载数据失败
      ElMessage.error(response.data.errorMessage);
    }
  });
};


// 页面初始化时加载数据：使用 Vue 生命周期的 onMounted 环境
onMounted(()=>{
  loadUserlist();
});

</script>

<template>
  <div>
    <el-button type="primary" size="large" @click="handleAdd()">添加</el-button>
    <el-table v-loading="loading" :data="userlist" >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="用户名" align="center" prop="username" />
      <el-table-column label="邮箱" align="center" prop="email" />
      <el-table-column label="密码" align="center" prop="password" />
      <el-table-column label="呢称" align="center" prop="nickname" />
      <el-table-column label="角色" align="center" prop="role" />
      <el-table-column label="状态" align="center" prop="status" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button type="default" size="large" @click="doEdit(scope.row)">更新</el-button>
          <el-button type="danger" size="large" @click="doRemove(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改用户对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form :model="userform" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userform.username" placeholder="用户名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userform.email" placeholder="邮箱" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="userform.password" placeholder="密码" />
        </el-form-item>
        <el-form-item label="呢称" prop="nickname">
          <el-input v-model="userform.nickname" placeholder="呢称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
<style scoped>
</style>