<script setup>

// ===================导入区===================
import axios from "axios";
import {onMounted,ref, toRefs,reactive} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";

// 表单引用，用于表单验证
const userFormRef = ref();

const open = ref(false);
const title = ref("");

// 角色选项
const roleOptions = [
  { label: '管理员', value: 'ADMIN' },
  { label: '普通用户', value: 'USER' },
  { label: '访客', value: 'GUEST' }
];

// 状态选项
const statusOptions = [
  { label: '正常', value: 1 },
  { label: '禁用', value: 0 },
  { label: '待审核', value: 2 }
];

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
      username: [
        { required: true, message: '用户名不能为空', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度应在3-20个字符之间', trigger: 'blur' },
        { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
      ],
      email: [
        { required: true, message: '邮箱不能为空', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱格式', trigger: ['blur', 'change'] }
      ],
      password: [
        { required: true, message: '密码不能为空', trigger: 'blur' },
        { min: 5, max: 20, message: '密码长度应在5-20个字符之间', trigger: 'blur' }
      ],
      nickname: [
        { required: true, message: '昵称不能为空', trigger: 'blur' },
        { min: 2, max: 10, message: '昵称长度应在2-10个字符之间', trigger: 'blur' }
      ],
      role: [
        { required: true, message: '请选择角色', trigger: 'change' }
      ],
      status: [
        { required: true, message: '请选择状态', trigger: 'change' }
      ]
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

  /** 提交按钮 - 添加表单验证 */
function submitForm() {
  if (!userFormRef.value) return;
  // 执行表单验证
  userFormRef.value.validate((valid, fields) => {
    if (valid) {
      // 验证通过，执行提交操作
      if (userform.value.id != null) {
        updateUserSave();
      } else {
        addUserSave();
      }
    } else {
      // 验证失败
      ElMessage.error('请检查表单填写是否正确');
      console.log('表单验证失败:', fields);
    }
  });
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
let userlist = ref([]);     // 存储用户列表的响应式数组
let loadUserlist=()=>{

  axios({
    "url":"/api/users/doUserListByPage",
    "method": "get",
    "params": queryParams.value
  }).then((response)=>{
    if(response.data.processResult=="SUCCESS"){
      ElMessage({
        message:'加载列表成功！',
        type:'success'
      });
      userlist.value=response.data.queryResultData.records;
      total.value = response.data.queryResultData.total;
    }
    if(response.data.processResult=="FAILED"){
      ElMessage.error(response.data.errorMessage);
    }
  })

}

const total = ref(0)

// 查询操作
const handleQuery = () => {
  queryParams.pageNum = 1
  loadUserlist()
}

// 重置查询条件
const handleReset = () => {
  queryParams.username = ''
  queryParams.role = ''
  queryParams.status = null
  queryParams.pageNum = 1
  loadUserlist()
}


// 分页大小变化
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  queryParams.pageNum = 1
  loadUserlist()
}

// 当前页变化
const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  loadUserlist()
}


// 页面初始化时加载数据：使用 Vue 生命周期的 onMounted 环境
onMounted(()=>{
  loadUserlist();
});

</script>

<template>
<!-- 查询条件区域 -->
  <el-card class="search-card" shadow="hover">
    <el-form :model="queryParams" :inline="true" label-width="80px">
      <el-form-item label="用户名">
        <el-input
            v-model="queryParams.username"
            placeholder="请输入用户名"
            clearable
            style="width: 200px"
        />
      </el-form-item>

      <el-form-item label="角色">
        <el-select
            v-model="queryParams.role"
            placeholder="请选择角色"
            clearable
            style="width: 150px"
        >
          <el-option
              v-for="option in roleOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="状态">
        <el-select
            v-model="queryParams.status"
            placeholder="请选择状态"
            clearable
            style="width: 150px"
        >
          <el-option
              v-for="option in statusOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
  </el-card>
    <!-- 操作按钮区域 -->
    <el-card class="table-card" shadow="hover">
      <div class="table-header">
        <div class="header-left">
          <el-button type="primary" icon="Plus" @click="handleAdd">添加用户</el-button>

        </div>
      </div>
    </el-card>
  <div>
    <el-table v-loading="loading" :data="userlist" >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="用户名" align="center" prop="username" />
      <el-table-column label="邮箱" align="center" prop="email" />
      <el-table-column label="密码" align="center" prop="password" />
      <el-table-column label="呢称" align="center" prop="nickname" />
      <el-table-column label="角色" align="center" prop="role">
            <template #default="scope">
              <el-tag :type="scope.row.role === 'ADMIN' ? 'danger' : scope.row.role === 'USER' ? 'PRIMARY' : 'info'">
                {{ roleOptions.find(item => item.value === scope.row.role)?.label || scope.row.role }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" align="center" prop="status">
            <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : scope.row.status === 0 ? 'danger' : 'warning'">
                {{ statusOptions.find(item => item.value === scope.row.status)?.label || scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button type="default" size="large" @click="doEdit(scope.row)">更新</el-button>
          <el-button type="danger" size="large" @click="doRemove(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

      <div class="pagination-container">
        <el-pagination
            v-model:current-page="queryParams.pageNum"
            v-model:page-size="queryParams.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />
      </div>

    <!-- 添加或修改用户对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="userFormRef" :model="userform" :rules="rules" label-width="80px">
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
        <el-form-item label="角色" prop="role">
                <el-select
                    v-model="userform.role"
                    placeholder="请选择角色"
                    clearable
                    style="width: 100%"
                >
                  <el-option
                      v-for="option in roleOptions"
                      :key="option.value"
                      :label="option.label"
                      :value="option.value"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="状态" prop="status">
                <el-select
                    v-model="userform.status"
                    placeholder="请选择状态"
                    clearable
                    style="width: 100%"
                >
                  <el-option
                      v-for="option in statusOptions"
                      :key="option.value"
                      :label="option.label"
                      :value="option.value"
                  />
                </el-select>
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