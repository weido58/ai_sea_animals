<script setup>
import userApi from '@/api/userApi.js' // 导入用户API服务
import { onMounted, ref, toRefs, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";

// 表单引用，用于表单验证
const userFormRef = ref();
const open = ref(false);     // 控制对话框的打开/关闭状态
const title = ref("");       // 对话框标题
const total = ref(0) //总记录条数

const data = reactive({
  userform: {               // 用户表单数据
    id: null,
    username: null,
    email: null,
    password: null,
    nickname: null,
    avatarUrl: '',
    role: null,
    status: null
  },
  queryParams: {            // 查询参数（用于分页和搜索）
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
      { min: 6, max: 20, message: '密码长度应在6-20个字符之间', trigger: 'blur' },
      {
        pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]{6,}$/,
        message: '密码必须包含至少一个大写字母、一个小写字母和一个数字',
        trigger: 'blur'
      }
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

const { queryParams, userform, rules } = toRefs(data);

// 重置表单
function reset() {
  userform.value = {
    id: null,
    username: null,
    email: null,
    password: null,
    nickname: null,
    avatarUrl: '',
    role: null,
    status: null
  };
}

// 添加用户处理
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

// 编辑用户
const doEdit = async (currentLineData) => {
  reset();
  const _id = currentLineData.id;

  try {
    const response = await userApi.getUserById(_id);
    userform.value = response.queryResultData;
    open.value = true;
    title.value = "修改用户";
  } catch (error) {
    console.error('获取用户信息失败:', error);
  }
}

// 表单提交
function submitForm() {
  if (!userFormRef.value) return;

  userFormRef.value.validate((valid, fields) => {
    if (valid) {
      if (userform.value.id != null) {
        updateUserSave();
      } else {
        addUserSave();
      }
    } else {
      ElMessage.error('请检查表单填写是否正确');
      console.log('表单验证失败:', fields);
    }
  });
}

// 添加用户
const addUserSave = async () => {
  try {
    await userApi.addUser(userform.value);
    ElMessage.success('添加用户成功！');
    open.value = false;
    loadUserlist();
  } catch (error) {
    console.error('添加用户失败:', error);
  }
}

// 更新用户
const updateUserSave = async () => {
  try {
    await userApi.updateUser(userform.value);
    ElMessage.success('更新用户成功！');
    open.value = false;
    loadUserlist();
  } catch (error) {
    console.error('更新用户失败:', error);
  }
}

// 用户列表
let userlist = ref([]);

// 加载用户列表
const loadUserlist = async () => {
  try {
    const response = await userApi.getUserListByPage(queryParams.value);
    userlist.value = response.queryResultData.records;
    total.value = response.queryResultData.total;
    ElMessage.success('加载列表成功！');
  } catch (error) {
    console.error('加载用户列表失败:', error);
  }
}

// 删除用户
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
        `确定要删除用户"${row.username}"吗？`,
        '删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
    );

    await userApi.deleteUser(row.id);
    ElMessage.success('删除成功！');
    loadUserlist();
  } catch (error) {
    if (error === 'cancel') {
      ElMessage.info('已取消删除');
    } else {
      console.error('删除用户失败:', error);
    }
  }
}

// 更新用户状态
const handleStatusChange = async (row) => {
  try {
    await userApi.updateUserStatus(row.id, row.status);
    ElMessage.success('状态更新成功！');
  } catch (error) {
    console.error('状态更新失败:', error);
    // 恢复原状态
    loadUserlist();
  }
}

onMounted(() => {
  loadUserlist();
});

// 角色选项
const roleOptions = [
  { label: '管理员', value: 'admin' },
  { label: '普通用户', value: 'user' },
  { label: '访客', value: 'guest' }
];

// 状态选项
const statusOptions = [
  { label: '正常', value: 1 },
  { label: '禁用', value: 0 },
  { label: '待审核', value: 2 }
];

// 上传相关配置
const uploadAction = '/api/upload/avatar'
const uploadHeaders = {}

// 头像上传成功回调
const handleAvatarSuccess = (response, file) => {
  if (response.processResult === "SUCCESS") {
    userform.value.avatarUrl = response.queryResultData;
    ElMessage.success('头像上传成功！')
  } else {
    ElMessage.error(response.message || '头像上传失败！')
  }
}

// 头像上传前验证
const beforeAvatarUpload = (file) => {
  const isJPGOrPNG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPGOrPNG) {
    ElMessage.error('头像图片只能是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 查询操作
const handleQuery = () => {
  queryParams.value.pageNum = 1
  loadUserlist()
}

// 重置查询条件
const handleReset = () => {
  queryParams.value.username = ''
  queryParams.value.role = ''
  queryParams.value.status = null
  queryParams.value.pageNum = 1
  loadUserlist()
}

// 分页大小变化
const handleSizeChange = (val) => {
  queryParams.value.pageSize = val
  queryParams.value.pageNum = 1
  loadUserlist()
}

// 当前页变化
const handleCurrentChange = (val) => {
  queryParams.value.pageNum = val
  loadUserlist()
}
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