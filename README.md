# 🚀 Docify – Document Conversion SaaS

> Nền tảng chuyển đổi tài liệu trực tuyến tốc độ cao, hỗ trợ đa định dạng, kiến trúc scalable và xử lý bất đồng bộ bằng Message Queue.

---

<p align="center">
  <img src="https://img.shields.io/badge/Next.js-14-black?style=for-the-badge&logo=next.js" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=springboot" />
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk" />
  <img src="https://img.shields.io/badge/RabbitMQ-FF6600?style=for-the-badge&logo=rabbitmq" />
  <img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql" />
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker" />
</p>

---

# ✨ Giới thiệu

**Docify** là hệ thống chuyển đổi tài liệu trực tuyến tương tự Smallpdf hoặc iLovePDF, cho phép người dùng chuyển đổi giữa nhiều định dạng tài liệu với độ chính xác cao và khả năng xử lý mạnh mẽ.

Hệ thống được thiết kế theo mô hình:

- ⚡ High Performance
- 🔄 Asynchronous Processing
- 🧩 Microservices Architecture
- 🔐 Secure File Handling
- 📈 Scalable Infrastructure

---

# 🎯 Tính năng chính

## 📄 Các luồng chuyển đổi hỗ trợ

| Input | Output |
|---|---|
| PPTX | PDF |
| Markdown | DOCX |
| DOCX | Markdown |
| Markdown | PDF |
| PDF | Markdown |
| PDF | DOCX |

---

# 🏗️ Kiến trúc hệ thống

```bash
                    +------------------+
                    |     Frontend     |
                    |   Next.js App    |
                    +---------+--------+
                              |
                              v
                    +------------------+
                    |   Spring Boot    |
                    |    Backend API   |
                    +---------+--------+
                              |
                              v
                    +------------------+
                    |     RabbitMQ     |
                    |   Message Queue  |
                    +---------+--------+
                              |
              +---------------+----------------+
              |                                |
              v                                v
      +---------------+              +----------------+
      | Pandoc Engine |              | Gotenberg API  |
      +---------------+              +----------------+
              |
              v
      +----------------+
      | File Storage   |
      | S3 / Local FS  |
      +----------------+
```

---

# 🧱 Tech Stack

| Layer | Công nghệ |
|---|---|
| Frontend | Next.js 14, React, TailwindCSS, TypeScript |
| Backend | Java 21, Spring Boot 3 |
| Queue | RabbitMQ |
| Workers | Java Workers |
| Conversion Engine | Pandoc, Gotenberg |
| Database | PostgreSQL |
| Cache | Redis |
| Storage | Amazon S3 / Local Storage |
| Containerization | Docker |

---

# 📁 Cấu trúc thư mục

```bash
docify/
├── frontend/              # Next.js frontend
├── backend/               # Spring Boot API
├── worker/                # Queue workers
├── docker/                # Docker compose files
├── storage/               # Temporary storage
├── scripts/               # Automation scripts
└── README.md
```

---

# ⚙️ Yêu cầu hệ thống

Trước khi chạy project cần cài đặt:

- Node.js >= 20
- Java JDK 21
- Maven >= 3.9
- Docker & Docker Compose
- PostgreSQL
- Redis
- RabbitMQ

---

# 🚀 Cài đặt dự án

# 1️⃣ Clone Repository

```bash
git clone https://github.com/your-username/docify.git
cd docify
```

---

# 2️⃣ Chạy Docker Services

Tạo file:

```bash
docker-compose.yml
```

```yaml
version: "3.9"

services:
  postgres:
    image: postgres:16
    container_name: docify-postgres
    environment:
      POSTGRES_DB: docify
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"

  redis:
    image: redis:7
    container_name: docify-redis
    ports:
      - "6379:6379"

  rabbitmq:
    image: rabbitmq:3-management
    container_name: docify-rabbitmq
    ports:
      - "5672:5672"
      - "15672:15672"

  gotenberg:
    image: gotenberg/gotenberg:8
    container_name: docify-gotenberg
    ports:
      - "3000:3000"
```

Khởi động:

```bash
docker compose up -d
```

---

# 3️⃣ Cài đặt Frontend

```bash
cd frontend
npm install
```

Tạo file:

```bash
.env.local
```

```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

Chạy frontend:

```bash
npm run dev
```

Frontend chạy tại:

```bash
http://localhost:3000
```

---

# 4️⃣ Cài đặt Backend

```bash
cd backend
```

Tạo file:

```bash
src/main/resources/application.yml
```

```yaml
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
```

Build project:

```bash
mvn clean install
```

Run server:

```bash
mvn spring-boot:run
```

Backend chạy tại:

```bash
http://localhost:8080
```

---

# 5️⃣ Cài đặt Pandoc

## Ubuntu / Debian

```bash
sudo apt install pandoc
```

## MacOS

```bash
brew install pandoc
```

Kiểm tra:

```bash
pandoc --version
```

---

# 6️⃣ Chạy Worker Service

```bash
cd worker
mvn spring-boot:run
```

---

# 📡 API Design

## Upload File

```http
POST /api/v1/convert
```

### Response

```json
{
  "success": true,
  "data": {
    "jobId": "uuid-value"
  },
  "error": null,
  "meta": {
    "timestamp": "2026-05-12T10:00:00Z"
  }
}
```

---

## Kiểm tra trạng thái Job

```http
GET /api/v1/jobs/{jobId}
```

---

# 🔄 Workflow xử lý

```bash
User Upload File
        ↓
Frontend Validate
        ↓
Spring Boot API
        ↓
Store File (S3/Local)
        ↓
Push Job → RabbitMQ
        ↓
Worker Consume Job
        ↓
Pandoc / Gotenberg
        ↓
Save Result File
        ↓
Return Download URL
```

---

# 🔐 Security & Optimization

- ✅ File Validation
- ✅ MIME Type Checking
- ✅ Rate Limiting với Redis
- ✅ Temporary File TTL
- ✅ Queue chống overload
- ✅ Async Processing
- ✅ Dockerized Services

---

# 📈 Roadmap

- [ ] Authentication & Authorization
- [ ] Premium Subscription
- [ ] OCR PDF
- [ ] AI Summarization
- [ ] Batch Conversion
- [ ] Google Drive Integration
- [ ] Dropbox Integration
- [ ] WebSocket Real-time Progress

---

# 🧪 Conversion Engines

| Engine | Chức năng |
|---|---|
| Pandoc | Markdown ↔ DOCX/PDF |
| Gotenberg | PPTX/DOCX → PDF |
| PDFBox | PDF Parsing |
| Aspose / Adobe API | PDF → DOCX chất lượng cao |

---

# 🐳 Docker Commands

## Xem containers

```bash
docker ps
```

## Dừng services

```bash
docker compose down
```

## Restart services

```bash
docker compose restart
```

---

# 👨‍💻 Development Commands

## Frontend

```bash
npm run dev
```

## Backend

```bash
mvn spring-boot:run
```

## Build Backend

```bash
mvn clean package
```

---

# 🤝 Contributing

```bash
git checkout -b feature/your-feature
git commit -m "feat: add new feature"
git push origin feature/your-feature
```

---

# 📄 License

MIT License © 2026 Docify

---

# ❤️ Credits

Built with:

- Spring Boot
- Next.js
- RabbitMQ
- Pandoc
- Gotenberg
- Docker

---

# 📌 References

- Docify Architecture Documentation
- System Design Notes
- Pandoc Official Docs
- Gotenberg Documentation

---
