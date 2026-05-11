Docify – Document Conversion SaaS

Docify là nền tảng chuyển đổi tài liệu trực tuyến hỗ trợ nhiều định dạng phổ biến như PDF, DOCX, Markdown và PowerPoint. Hệ thống được thiết kế theo kiến trúc microservices kết hợp message queue nhằm đảm bảo khả năng xử lý bất đồng bộ, tốc độ cao và ổn định khi có nhiều người dùng đồng thời.

✨ Tính năng nổi bật
Chuyển đổi tài liệu nhanh chóng và chính xác
Hỗ trợ kéo thả file trực tiếp
Theo dõi trạng thái xử lý theo thời gian thực
Tự động xóa file sau thời gian TTL để đảm bảo riêng tư
Kiến trúc scalable với RabbitMQ và Worker Engines
Hỗ trợ nhiều định dạng tài liệu phổ biến
Các luồng chuyển đổi hiện hỗ trợ
Input	Output
PPTX	PDF
Markdown	DOCX
DOCX	Markdown
Markdown	PDF
PDF	Markdown
PDF	DOCX
🏗️ Kiến trúc hệ thống

Hệ thống được chia thành nhiều lớp độc lập:

Layer	Công nghệ	Vai trò
Frontend	Next.js 14, React, TailwindCSS, TypeScript	UI/UX
Backend API	Java 21, Spring Boot 3	Điều phối job
Message Broker	RabbitMQ	Queue xử lý
Worker Engines	Java Worker, Pandoc, Gotenberg	Chuyển đổi file
Database	PostgreSQL	Lưu metadata
Cache	Redis	Rate limit & cache
Storage	Amazon S3 / Local Storage	Lưu file

Thông tin kiến trúc được xây dựng dựa trên tài liệu thiết kế hệ thống Docify.

📁 Cấu trúc thư mục đề xuất
docify/
├── frontend/          # Next.js frontend
├── backend/           # Spring Boot API
├── worker/            # Worker xử lý queue
├── docker/            # Docker compose & config
├── storage/           # File tạm local
└── README.md
⚙️ Yêu cầu hệ thống

Trước khi cài đặt, cần chuẩn bị:

Node.js >= 20
Java JDK 21
Maven >= 3.9
Docker & Docker Compose
PostgreSQL
Redis
RabbitMQ
🚀 Hướng dẫn cài đặt
1. Clone project
git clone https://github.com/your-username/docify.git
cd docify
🐳 Chạy các service phụ trợ bằng Docker

Tạo file docker-compose.yml

version: "3.9"

services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: docify
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"

  redis:
    image: redis:7
    ports:
      - "6379:6379"

  rabbitmq:
    image: rabbitmq:3-management
    ports:
      - "5672:5672"
      - "15672:15672"

  gotenberg:
    image: gotenberg/gotenberg:8
    ports:
      - "3000:3000"

Khởi động:

docker compose up -d
🖥️ Cài đặt Frontend

Di chuyển vào thư mục frontend:

cd frontend

Cài dependencies:

npm install

Tạo file .env.local

NEXT_PUBLIC_API_URL=http://localhost:8080/api

Chạy frontend:

npm run dev

Frontend chạy tại:

http://localhost:3000
☕ Cài đặt Backend API

Di chuyển vào backend:

cd backend

Tạo file application.yml

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/docify
    username: postgres
    password: postgres

  rabbitmq:
    host: localhost
    port: 5672

  data:
    redis:
      host: localhost
      port: 6379

Build project:

mvn clean install

Chạy backend:

mvn spring-boot:run

Backend chạy tại:

http://localhost:8080
⚙️ Cài đặt Pandoc
Ubuntu/Debian
sudo apt install pandoc
MacOS
brew install pandoc

Kiểm tra:

pandoc --version
🔄 Chạy Worker Service

Worker chịu trách nhiệm lấy job từ RabbitMQ và thực hiện convert file.

cd worker
mvn spring-boot:run
📡 API mẫu
Upload file
POST /api/v1/convert

Response:

{
  "success": true,
  "data": {
    "jobId": "uuid-value"
  },
  "error": null
}
Kiểm tra trạng thái
GET /api/v1/jobs/{jobId}
🔁 Workflow xử lý
User upload file
Backend validate và lưu file tạm
Tạo Conversion Job
Đẩy job vào RabbitMQ
Worker lấy job và xử lý convert
Upload kết quả
Trả link download cho user

Luồng xử lý này được mô tả trong tài liệu workflow của hệ thống.

🔐 Bảo mật & tối ưu
Validate MIME type
Giới hạn kích thước file upload
TTL auto-delete file sau 1 giờ
Rate limiting bằng Redis
Queue chống quá tải hệ thống
Sandbox engine convert
📈 Định hướng phát triển
Authentication & User Quota
Subscription/Premium Plan
OCR PDF
AI Document Summarization
Batch Conversion
Google Drive / Dropbox Integration
Real-time WebSocket progress
📚 Công nghệ chuyển đổi
Công cụ	Vai trò
Pandoc	Markdown ↔ DOCX/PDF
Gotenberg	PPTX/DOCX → PDF
PDFBox	PDF parsing
Aspose / Adobe API	PDF → DOCX chất lượng cao

Thông tin engine chuyển đổi được lấy từ tài liệu ý tưởng hệ thống.

👨‍💻 Đóng góp
git checkout -b feature/your-feature
git commit -m "feat: add new feature"
git push origin feature/your-feature
