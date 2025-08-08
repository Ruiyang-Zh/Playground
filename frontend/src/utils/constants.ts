import type { SportsType, ActivityStatus } from '@/types/activity'

export const SPORTS_OPTIONS: Array<{ label: string; value: SportsType }> = [
  { label: '篮球', value: 'BASKETBALL' },
  { label: '羽毛球', value: 'BADMINTON' },
  { label: '跑步', value: 'RUNNING' },
  { label: '游泳', value: 'SWIMMING' },
  { label: '足球', value: 'FOOTBALL' },
  { label: '网球', value: 'TENNIS' },
  { label: '乒乓球', value: 'TABLE_TENNIS' },
  { label: '骑行', value: 'CYCLING' },
  { label: '徒步', value: 'HIKING' },
  { label: '瑜伽', value: 'YOGA' },
  { label: '健身', value: 'FITNESS' },
  { label: '其他', value: 'OTHER' }
]

export const STATUS_OPTIONS: Array<{ label: string; value: ActivityStatus }> = [
  { label: '招募中', value: 'RECRUITING' },
  { label: '已满员', value: 'FULL' },
  { label: '报名结束', value: 'REGISTRATION_CLOSED' },
  { label: '进行中', value: 'IN_PROGRESS' },
  { label: '已结束', value: 'COMPLETED' },
  { label: '已取消', value: 'CANCELLED' }
]

export const SORT_OPTIONS = [
  { label: '开始时间', value: 'startTime' },
  { label: '创建时间', value: 'createdAt' }
]

export const SORT_DIR_OPTIONS = [
  { label: '升序', value: 'asc' },
  { label: '降序', value: 'desc' }
]

export const LOCATION_OPTIONS = [
  {
    label: "北京",
    value: "北京",
    children: [
      {
        label: "北京市",
        value: "北京市",
        children: [
          { label: "东城区", value: "东城区" },
          { label: "西城区", value: "西城区" },
          { label: "朝阳区", value: "朝阳区" },
          { label: "海淀区", value: "海淀区" },
          { label: "丰台区", value: "丰台区" },
          { label: "石景山区", value: "石景山区" },
          { label: "门头沟区", value: "门头沟区" },
          { label: "房山区", value: "房山区" },
          { label: "通州区", value: "通州区" },
          { label: "顺义区", value: "顺义区" },
          { label: "昌平区", value: "昌平区" },
          { label: "大兴区", value: "大兴区" },
          { label: "怀柔区", value: "怀柔区" },
          { label: "平谷区", value: "平谷区" },
          { label: "密云区", value: "密云区" },
          { label: "延庆区", value: "延庆区" }
        ]
      }
    ]
  },
  {
    label: "上海",
    value: "上海",
    children: [
      {
        label: "上海市",
        value: "上海市",
        children: [
          { label: "黄浦区", value: "黄浦区" },
          { label: "徐汇区", value: "徐汇区" },
          { label: "长宁区", value: "长宁区" },
          { label: "静安区", value: "静安区" },
          { label: "普陀区", value: "普陀区" },
          { label: "虹口区", value: "虹口区" },
          { label: "杨浦区", value: "杨浦区" },
          { label: "浦东新区", value: "浦东新区" },
          { label: "闵行区", value: "闵行区" },
          { label: "宝山区", value: "宝山区" },
          { label: "嘉定区", value: "嘉定区" },
          { label: "金山区", value: "金山区" },
          { label: "松江区", value: "松江区" },
          { label: "青浦区", value: "青浦区" },
          { label: "奉贤区", value: "奉贤区" },
          { label: "崇明区", value: "崇明区" }
        ]
      }
    ]
  },
  {
    label: "江苏",
    value: "江苏",
    children: [
      {
        label: "南京市",
        value: "南京市",
        children: [
          { label: "玄武区", value: "玄武区" },
          { label: "秦淮区", value: "秦淮区" },
          { label: "建邺区", value: "建邺区" },
          { label: "鼓楼区", value: "鼓楼区" },
          { label: "浦口区", value: "浦口区" },
          { label: "栖霞区", value: "栖霞区" },
          { label: "雨花台区", value: "雨花台区" },
          { label: "江宁区", value: "江宁区" },
          { label: "六合区", value: "六合区" },
          { label: "溧水区", value: "溧水区" },
          { label: "高淳区", value: "高淳区" }
        ]
      },
      {
        label: "苏州市",
        value: "苏州市",
        children: [
          { label: "姑苏区", value: "姑苏区" },
          { label: "虎丘区", value: "虎丘区" },
          { label: "吴中区", value: "吴中区" },
          { label: "相城区", value: "相城区" },
          { label: "吴江区", value: "吴江区" },
          { label: "常熟市", value: "常熟市" },
          { label: "张家港市", value: "张家港市" },
          { label: "昆山市", value: "昆山市" },
          { label: "太仓市", value: "太仓市" }
        ]
      }
      // 江苏其他城市可以继续补充...
    ]
  },
  {
    label: "浙江",
    value: "浙江",
    children: [
      {
        label: "杭州市",
        value: "杭州市",
        children: [
          { label: "上城区", value: "上城区" },
          { label: "下城区", value: "下城区" },
          { label: "江干区", value: "江干区" },
          { label: "拱墅区", value: "拱墅区" },
          { label: "西湖区", value: "西湖区" },
          { label: "滨江区", value: "滨江区" },
          { label: "萧山区", value: "萧山区" },
          { label: "余杭区", value: "余杭区" },
          { label: "富阳区", value: "富阳区" },
          { label: "临安区", value: "临安区" },
          { label: "桐庐县", value: "桐庐县" },
          { label: "淳安县", value: "淳安县" },
          { label: "建德市", value: "建德市" }
        ]
      },
      {
        label: "宁波市",
        value: "宁波市",
        children: [
          { label: "海曙区", value: "海曙区" },
          { label: "江北区", value: "江北区" },
          { label: "北仑区", value: "北仑区" },
          { label: "镇海区", value: "镇海区" },
          { label: "鄞州区", value: "鄞州区" },
          { label: "奉化区", value: "奉化区" },
          { label: "象山县", value: "象山县" },
          { label: "宁海县", value: "宁海县" },
          { label: "余姚市", value: "余姚市" },
          { label: "慈溪市", value: "慈溪市" }
        ]
      }
      // 浙江其他城市可以继续补充...
    ]
  }
]
