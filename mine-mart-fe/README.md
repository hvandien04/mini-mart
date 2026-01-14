# Mini Mart Frontend

Frontend application cho hệ thống quản lý kho Mini Mart, được xây dựng với React + TypeScript + Vite.

## Công nghệ

- **React 19** - UI framework
- **TypeScript** - Type safety
- **Vite** - Build tool
- **React Router** - Routing
- **TanStack Query (React Query)** - Data fetching & state management
- **Axios** - HTTP client
- **Ant Design** - UI component library
- **Day.js** - Date manipulation

## Cài đặt

### Development (Local)

1. Cài đặt dependencies:
```bash
npm install
```

2. Chạy development server:
```bash
npm run dev
```

3. Build production:
```bash
npm run build
```

### Docker

1. Build và chạy với Docker Compose (từ root project):
```bash
docker-compose up --build
```

2. Hoặc chỉ build frontend:
```bash
cd mine-mart-fe
docker build -t frontend-app .
docker run -p 3000:80 frontend-app
```

Frontend sẽ chạy tại `http://localhost:3000`

## Cấu trúc thư mục

```
src/
├─ api/              # API layer (Axios calls)
├─ types/            # TypeScript types (map 1-1 với DTOs backend)
├─ hooks/            # React Query hooks
├─ pages/            # Page components
│  ├─ login/
│  ├─ categories/
│  ├─ products/
│  ├─ imports/
│  ├─ exports/
│  └─ reports/
├─ components/       # Reusable components
│  └─ layout/
├─ router/           # Router configuration với guards
├─ utils/            # Utilities (auth, error handling, axios)
└─ App.tsx           # Main app component
```

## Tính năng

### Authentication & Authorization
- Login với username/password
- JWT token management (localStorage)
- Route guards (Admin/Staff)
- Auto logout khi 401

### Quản lý Danh mục
- CRUD categories (Admin only)
- Active/Inactive status

### Quản lý Sản phẩm
- CRUD products (Admin only)
- Lọc theo danh mục
- Hiển thị tồn kho hiện tại

### Nhập kho
- Tạo phiếu nhập với nhiều mặt hàng
- Mỗi item tạo thành một lô hàng (batch) với:
  - Số lượng nhập
  - Số lượng còn lại (ban đầu = số lượng nhập)
  - Hạn sử dụng

### Xuất kho
- Tạo phiếu xuất
- **Backend tự động xử lý FIFO + hạn sử dụng**
- FE chỉ gửi productId và quantity
- Backend tự động:
  - Ưu tiên xuất các lô có hạn sử dụng gần nhất
  - Nếu 1 lô không đủ → tự động tách sang lô tiếp theo
  - Tự động tạo nhiều ExportReceiptItem nếu cần
  - Validate tồn kho

### Báo cáo
- Tồn kho hiện tại (group theo product)
- Danh sách hàng sắp hết hạn (highlight)
- Tổng nhập - xuất theo khoảng thời gian

## API Configuration

### Development
- Backend API được proxy qua Vite dev server: `http://localhost:8080/api`

### Production (Docker)
- Nginx proxy `/api` requests đến backend service `springboot:8080`
- Frontend serve tại port 80 (mapped to host port 3000)

## Default Login

- Username: `admin`
- Password: `admin123`

## Docker

### Dockerfile
- Multi-stage build:
  - Stage 1: Build React app với Node.js
  - Stage 2: Serve static files với Nginx

### Nginx Configuration
- Proxy `/api` requests đến backend
- Serve static files với SPA routing support
- Gzip compression
- Security headers
- Cache static assets

## Lưu ý

- Frontend tuân thủ tuyệt đối API và DTOs từ backend
- Không hardcode dữ liệu
- Types map 1-1 với DTOs backend
- Error handling tự động hiển thị message từ backend
- Trong Docker, frontend và backend giao tiếp qua Docker network
