# 🚀 Quick Start Guide - Custom Drools UI

## 📖 Introduction

**Custom UI for Drools 10** is a custom user interface developed for the Drools 10 engine, specifically designed to support **WCO Goods Declaration and Cargo Report v4.2.0**.

### 🎯 Objectives

This project provides an intuitive and user-friendly interface to:
- Manage and edit business rules in Drools
- Track version history of rules
- Support change request approval workflows
- Integrate with WCO Goods Declaration and Cargo Report standards

### 🏗️ Architecture

```
├── Backend (Spring Boot + Drools 10)
│   ├── Business Rules Engine
│   ├── WCO Data Processing
│   └── PostgreSQL Database
├── Frontend (Next.js + TypeScript)
│   ├── Rules Management UI
│   ├── Version Control
│   └── Change Request System
└── Sample Data (WCO 4.2.0 JSON)
    ├── Goods Declaration
    └── Cargo Report
```

### 📋 Key Features

- ✅ Business rules management with version control
- ✅ Intuitive rules editing interface
- ✅ Change history tracking
- ✅ Change request approval system
- ✅ Compatible with WCO standards v4.2.0

---

## Prerequisites

- Java 17+
- Node.js 18+
- PostgreSQL

---

## 🔧 Setup

### 1. Start Backend

```bash
cd backend

# Start database
docker compose up -d

# Start Spring Boot (JPA will auto-update schema)
./gradlew bootRun

# Backend runs on http://localhost:8080
```

### 2. Start Frontend

```bash
cd frontend

# Install dependencies
npm install

# Start Next.js dev server
npm run dev

# Frontend runs on http://localhost:3000
```

---