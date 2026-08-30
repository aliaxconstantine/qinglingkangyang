package com.aliax.qlky.bean.basebean;

public class FileInfo {
        private String originalName;
        private String storedName;
        private String fileType;
        private long size;
        private String storagePath;
        private String category;
        private String url;  // 完整的访问URL

        // 修改构造函数，接收baseUrl参数
        public FileInfo(String originalName, String storedName, String fileType,
                        long size, String storagePath, String category, String baseUrl) {
            this.originalName = originalName;
            this.storedName = storedName;
            this.fileType = fileType;
            this.size = size;
            this.storagePath = storagePath;
            this.category = category;
            this.url = buildFullUrl(baseUrl, storagePath);
        }

        private String buildFullUrl(String baseUrl, String filename) {
            return baseUrl + filename;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getOriginalName() {
            return originalName;
        }

        public void setOriginalName(String originalName) {
            this.originalName = originalName;
        }

        public String getStoredName() {
            return storedName;
        }

        public void setStoredName(String storedName) {
            this.storedName = storedName;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public String getStoragePath() {
            return storagePath;
        }

        public void setStoragePath(String storagePath) {
            this.storagePath = storagePath;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
    }