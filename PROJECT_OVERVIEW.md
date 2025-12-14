# Food Delivery 项目全景与知识点梳理

## 技术栈全览
- **后端**：Spring Boot 3、Spring Security + JWT、Spring Data JPA、Lombok、Maven、MySQL。
- **前端**：Vite、Vue 3（组合式 API）、Pinia、Vue Router、Element Plus、Axios。
- **部署**：Nginx（前端）、Dockerfile（前/后端）、docker-compose 提供 MySQL 8.3。
- **设计体系**：自定义 Design Token（颜色/圆角/间距/阴影），暖色调后台风格；卡片统一暖白底 + 轻边框 + 弱阴影。

## 角色与权限模型
- 角色：`USER`（消费者）、`MERCHANT`（商家）、`ADMIN`（平台）。
- 认证/鉴权：JWT 颁发与校验；Spring Security `@PreAuthorize` 控制接口；前端路由守卫按 token + 角色跳转。
- 访问控制：
  - 用户端仅看到审核通过且营业的店铺与上架菜品。
  - 商家端只能操作自己的店铺/菜品/订单；店铺需审核通过后可上架菜品、接单。
  - 管理端可全量管理用户/店铺/菜品/订单/轮播。

## 数据模型与数据库
- 实体（JPA，对应 MySQL 表）：
  - `User(id, username, password, role, phone, address, …)`
  - `Shop(id, name, description, address, online, status[PENDING/APPROVED/REJECTED], owner)`
  - `Dish(id, name, category, price, available, imageUrl, shop)`
  - `Order(id, user, shop, status, totalAmount, createdAt, …)`
  - `Carousel(id, shop, dish, imageUrl, sortOrder, status[PENDING/APPROVED/REJECTED/STOPPED], reviewer, reviewedAt, …)`
- 外键/约束：店铺归属 owner；菜品归属店铺；订单归属用户+店铺；轮播归属店铺+菜品，申请/播放上限校验。
- 配置：`application.yml` 指向 MySQL（默认 root/root，ddl-auto:none）；`app.carousel.max-playing` 控制轮播播放上限。

## 后端实现（主要接口与服务）
- 认证：`POST /api/auth/register | /login`；`GET /api/auth/me`；用户管理接口需 ADMIN。
- 店铺：CRUD（非查询需 ADMIN）；`/api/shops/mine`（商家自查）；审核 `/approve|reject`（ADMIN）；营业控制 `/online|offline`（owner/ADMIN）。
- 菜品：CRUD、上/下架 `/on|off`（ADMIN 或店主且店铺已审核）；`/shop/{shopId}` 按店铺查；图片上传 `/image`。
- 订单：下单 `/api/orders?shopId=`（用户）；支付/取消/接单/完成；`/my` 查看本人；`/shop/mine` 商家看本店；ADMIN 可全量查询/删除。
- 轮播：
  - 用户端公开：`GET /api/carousels/public`（限已审核播放，最多 5 条，含店铺/菜品信息）。
  - 商家端：`GET /api/carousels/mine` 查看申请；`POST /api/carousels/apply` 提交 dishId+shopId（校验店铺审核通过、同菜品不可重复 PENDING/APPROVED）。
  - 管理端：`GET /api/carousels` 全量查看；`POST /{id}/approve|reject|stop` 审核/下架（通过时校验播放上限）。
- 数据大屏：DashboardService 汇总用户数、菜品分类分布、订单状态分布、近7天趋势；商家端与管理员端各自调用。

## 前端实现与路由
- 路由结构：
  - 公共：`/login`、`/register`
  - 管理端：`/admin/dashboard`、`/admin/users`、`/admin/shops`、`/admin/dishes`、`/admin/orders`、`/admin/carousels`
  - 商家端：`/merchant/dashboard`、`/merchant/dishes`、`/merchant/orders`
  - 用户端：`/client/shops`、`/client/dishes`、`/client/orders`、`/client/profile`
- 状态管理（Pinia）：
  - `auth`：token、user、角色；路由守卫依赖。
  - `client`：shops/dishes/orders/carousels + loading；过滤仅展示审核通过&营业数据。
- 轮播前端逻辑：
  - 用户端 `ClientShops.vue`：顶部居中卡片式轮播，7:5 容器、cover 等比裁剪，限宽限高；数据来自 `client.carousels`（`/api/carousels/public`）。
  - 商家端 `Dishes.vue`：状态列读取 `/api/carousels`（ADMIN）或 `/api/carousels/mine`（商家），申请按钮调用 `/apply`，状态映射 PENDING/APPROVED/REJECTED/STOPPED。
  - 管理端 `Carousels.vue`：三列表（播放中/待审核/已拒绝），操作通过/拒绝/停止播放，超上限提示。
- 设计规范：Token 定义于 `src/assets/theme.css`（颜色/圆角/间距/阴影），统一暖白卡片、轻边框、弱阴影；按钮/标签按语义色；数据大屏与卡片布局统一。

## 主要页面与实现要点
- 登录/注册：Element Plus 表单；注册支持商家/普通用户；登录后依据角色跳转。
- 管理端：
  - 用户/商家/菜品/订单管理：表格 + 弹窗 CRUD；表单校验；上/下架、审核、删除等操作。
  - 数据大屏：KPI 卡片 + ECharts 图表（状态分布/分类分布/趋势）。
  - 轮播图管理：状态分组表格 + 操作按钮，播放上限校验。
- 商家端：
  - 菜品管理：表格内轮播申请按钮，状态列实时显示；店铺审核未通过时禁用操作。
  - 订单管理：接单/完成，店铺审核未通过时提示。
  - 数据大屏：商家自有经营概览。
- 用户端：
  - 商家列表：顶部精选轮播卡片；下方网格卡片显示商家名/分类/状态/地址；点击查看菜品。
  - 菜品页：按店铺筛选、加购/下单、订单提交。
  - 订单/个人中心：表格/信息卡片展示。

## 运行与调试
1. 启动 MySQL（或 `docker-compose up -d`）。
2. 后端：`cd backend && mvn spring-boot:run`（如使用自定义 DB/密钥，调整 `application.yml` 或环境变量）。
3. 前端：`cd frontend && npm install && npm run dev`，必要时设置 `VITE_API_BASE=http://localhost:8080`。
4. 账号：管理员 `admin/admin`，示例用户 `demo/demo`（若 DataInitializer 保留）。

## 重点知识点速查
- 安全与鉴权：JWT + Spring Security 过滤器链，自定义 `JwtAuthenticationFilter`；接口用 `@PreAuthorize`。
- JPA：实体关系映射（多对一 shop/owner、dish/shop、order/user&shop、carousel/shop&dish&reviewer），Repository 派生查询，Service 层事务控制。
- 前端状态与路由守卫：Pinia 管理 token/用户信息；router.beforeEach 校验角色并重定向。
- 设计系统：CSS 变量 Token，统一颜色/圆角/间距/阴影；暖色背景 + 轻量卡片；按钮与标签的语义色收敛。
- 轮播运营流：申请（商家）→ 审核/上限控制（管理员）→ 公共展示（用户端）→ 停止播放。前后端接口/状态同步、前端状态映射与错误提示。 
