import { invoke } from '@tauri-apps/api/core';

export interface EnvConfig {
  VITE_API_BASE_URL?: string;
  [key: string]: string | undefined;
}

class EnvUtil {
  private config: EnvConfig = {};
  private loaded = false;

  async loadConfig(): Promise<EnvConfig> {
    if (this.loaded) {
      return this.config;
    }

    try {
      // 调用 Rust 函数读取 .env 文件
      const envContent = await invoke<string>('read_env_file');
      this.config = this.parseEnvContent(envContent);
      this.loaded = true;

      console.log('✅ 环境配置加载成功:', this.config);
      return this.config;
    } catch (error) {
      console.warn('⚠️ 读取 .env 文件失败，使用默认配置:', error);
      // 使用默认配置
      this.config = {
        VITE_API_BASE_URL: 'http://localhost:8080/api'
      };
      this.loaded = true;
      return this.config;
    }
  }

  private parseEnvContent(content: string): EnvConfig {
    const config: EnvConfig = {};
    const lines = content.split('\n');

    for (const line of lines) {
      // 跳过空行和注释行
      if (line.trim() === '' || line.trim().startsWith('#')) {
        continue;
      }

      const [key, ...valueParts] = line.split('=');
      if (key && valueParts.length > 0) {
        const value = valueParts.join('=').trim();
        config[key.trim()] = value;
      }
    }

    return config;
  }

  // 获取 API 基础地址
  getApiBaseUrl(): string {
    return this.config.VITE_API_BASE_URL || 'http://localhost:8080/api';
  }

  // 获取任意配置项
  get(key: string): string | undefined {
    return this.config[key];
  }

  // 获取所有配置
  getAll(): EnvConfig {
    return { ...this.config };
  }

  // 检查是否已加载
  isLoaded(): boolean {
    return this.loaded;
  }
}

// 导出单例实例
export const envUtil = new EnvUtil();
