// 推荐放在常量文件 constants/fieldTypes.js
export const DB_FIELD_TYPES = [
    {
      id: '1',
      name: 'TEXT',
      dbType: 'TEXT',        // 实际数据库类型
      javaType: 'String',    // 对应Java类型
      length: null           // 长度限制（null表示不限制）
    },
    {
      id: '2',
      name: 'LONGTEXT',
      dbType: 'LONGTEXT',    // 长文本类型
      javaType: 'String',
      length: null
    },
    {
      id: '3',
      name: 'VARCHAR',
      dbType: 'VARCHAR',     // 选择框通常用可变字符串
      javaType: 'String',
      length: 255            // 默认长度
    },
    // 补充常见数据库类型
    {
      id: '4',
      name: 'INT',
      dbType: 'INT',
      javaType: 'Integer',
      length: 11
    },
    {
      id: '6',
      name: 'DATETIME',
      dbType: 'DATETIME',
      javaType: 'LocalDateTime',
      length: null
    },
    {
      id: '7',
      name: 'IMAGE',
      dbType: 'TEXT',
      javaType: 'String',
      length: 255
    }
  ]