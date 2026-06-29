import fs from "node:fs/promises";
import path from "node:path";
import { SpreadsheetFile, Workbook } from "@oai/artifact-tool";

const workDir = process.cwd();
const outputDir = path.join(workDir, "..", "system-function-template-20260628");
const outputPath = path.join(outputDir, "邻里互助-系统功能梳理模板.xlsx");
const previewDir = path.join(outputDir, "previews");

await fs.mkdir(outputDir, { recursive: true });
await fs.mkdir(previewDir, { recursive: true });

const workbook = Workbook.create();

const colors = {
  title: "#0F766E",
  titleText: "#FFFFFF",
  introFill: "#ECFDF5",
  sectionFill: "#D1FAE5",
  headerFill: "#134E4A",
  headerText: "#FFFFFF",
  requiredFill: "#FFF7ED",
  optionalFill: "#F8FAFC",
  border: "#CBD5E1",
  noteText: "#334155",
  placeholder: "#64748B",
  accent: "#F59E0B",
  mutedGreen: "#DCFCE7",
  mutedBlue: "#DBEAFE",
  mutedYellow: "#FEF3C7",
  mutedRed: "#FEE2E2",
};

function setTitle(sheet, title, subtitle, lastCol) {
  sheet.getRange(`A1:${lastCol}1`).merge();
  sheet.getRange("A1").values = [[title]];
  sheet.getRange(`A2:${lastCol}2`).merge();
  sheet.getRange("A2").values = [[subtitle]];
  sheet.getRange(`A1:${lastCol}2`).format = {
    fill: colors.title,
    font: { bold: true, color: colors.titleText, size: 15, name: "Microsoft YaHei" },
    horizontalAlignment: "left",
    verticalAlignment: "center",
    wrapText: true,
  };
  sheet.getRange("A1").format.rowHeight = 28;
  sheet.getRange("A2").format.rowHeight = 38;
  sheet.getRange("A2").format.font = { color: colors.titleText, size: 10, name: "Microsoft YaHei" };
}

function setIntro(sheet, startRow, lastCol, rows) {
  for (let i = 0; i < rows.length; i += 1) {
    const rowIndex = startRow + i;
    sheet.getRange(`A${rowIndex}`).values = [[rows[i][0]]];
    sheet.getRange(`B${rowIndex}:${lastCol}${rowIndex}`).merge();
    sheet.getRange(`B${rowIndex}`).values = [[rows[i][1]]];
  }

  const range = sheet.getRange(`A${startRow}:${lastCol}${startRow + rows.length - 1}`);
  range.format = {
    fill: colors.introFill,
    font: { color: colors.noteText, size: 10, name: "Microsoft YaHei" },
    wrapText: true,
    verticalAlignment: "top",
    borders: { preset: "all", style: "thin", color: colors.border },
  };
  sheet.getRange(`A${startRow}:A${startRow + rows.length - 1}`).format.font = {
    color: "#0F766E",
    size: 10,
    bold: true,
    name: "Microsoft YaHei",
  };
  sheet.getRange(`A${startRow}:${lastCol}${startRow + rows.length - 1}`).format.rowHeight = 24;
}

function setSection(sheet, rangeAddress, text) {
  const range = sheet.getRange(rangeAddress);
  range.merge();
  range.values = [[text]];
  range.format = {
    fill: colors.sectionFill,
    font: { bold: true, color: "#065F46", size: 10, name: "Microsoft YaHei" },
    borders: { preset: "outside", style: "thin", color: colors.border },
  };
}

function setHeader(sheet, rangeAddress, values) {
  const range = sheet.getRange(rangeAddress);
  range.values = [values];
  range.format = {
    fill: colors.headerFill,
    font: { bold: true, color: colors.headerText, size: 10, name: "Microsoft YaHei" },
    wrapText: true,
    horizontalAlignment: "center",
    verticalAlignment: "center",
    borders: { preset: "all", style: "thin", color: colors.border },
  };
  range.format.rowHeight = 38;
}

function formatBody(sheet, rangeAddress) {
  sheet.getRange(rangeAddress).format = {
    font: { name: "Microsoft YaHei", size: 10, color: "#0F172A" },
    verticalAlignment: "top",
    wrapText: true,
    borders: { preset: "all", style: "thin", color: colors.border },
  };
}

function setPlaceholder(sheet, rangeAddress) {
  sheet.getRange(rangeAddress).format.font = {
    name: "Microsoft YaHei",
    size: 10,
    color: colors.placeholder,
    italic: true,
  };
}

function applyCommonSheetSetup(sheet, usedRange, freezeRows) {
  sheet.showGridLines = false;
  sheet.freezePanes.freezeRows(freezeRows);
}

function setWidths(sheet, widths) {
  for (const [column, width] of Object.entries(widths)) {
    sheet.getRange(`${column}:${column}`).format.columnWidth = width;
  }
}

function applyListValidation(sheet, rangeAddress, values) {
  sheet.getRange(rangeAddress).dataValidation = {
    rule: {
      type: "list",
      values,
    },
  };
}

function addStatusHighlight(range) {
  range.conditionalFormats.add("containsText", {
    text: "高",
    format: { fill: "#FEE2E2", font: { bold: true, color: "#991B1B" } },
  });
  range.conditionalFormats.add("containsText", {
    text: "中",
    format: { fill: "#FEF3C7", font: { bold: true, color: "#92400E" } },
  });
  range.conditionalFormats.add("containsText", {
    text: "低",
    format: { fill: "#DCFCE7", font: { bold: true, color: "#166534" } },
  });
}

function addLifeCycleHighlight(range) {
  range.conditionalFormats.add("containsText", {
    text: "待梳理",
    format: { fill: "#E0F2FE", font: { color: "#075985", bold: true } },
  });
  range.conditionalFormats.add("containsText", {
    text: "已梳理",
    format: { fill: "#DCFCE7", font: { color: "#166534", bold: true } },
  });
  range.conditionalFormats.add("containsText", {
    text: "待确认",
    format: { fill: "#FEF3C7", font: { color: "#92400E", bold: true } },
  });
}

function buildAdminSheet() {
  const sheet = workbook.worksheets.add("管理端功能");
  setTitle(
    sheet,
    "管理端功能梳理模板",
    "用途：先梳理菜单、页面、按钮、权限、说明，再进入接口与开发。建议一行描述一个可落地功能点，避免多功能混在同一行。",
    "Q",
  );

  setIntro(sheet, 4, "Q", [
    ["填写建议", "1. 先拆父级菜单，再拆子菜单，再拆页面内列表与按钮。"],
    ["填写建议", "2. 按“一个页面 + 一类功能动作”粒度填写，便于后续拆接口与权限。"],
    ["填写建议", "3. 列表字段、查询条件、按钮动作、状态流转、权限码建议都写明。"],
    ["交付口径", "梳理完成后，应能直接支撑管理端菜单树、页面原型、接口清单与 Apifox 契约整理。"]],
  );

  setSection(sheet, "A9:Q9", "功能清单");
  const headers = [
    "序号",
    "父级菜单",
    "子菜单",
    "页面名称",
    "页面类型",
    "列表/区块名称",
    "查询条件",
    "列表字段",
    "按钮/操作",
    "功能动作说明",
    "前置条件/权限",
    "状态或结果",
    "关联接口/数据对象",
    "优先级",
    "梳理状态",
    "负责人",
    "备注",
  ];
  setHeader(sheet, "A10:Q10", headers);

  const sampleRows = [
    [1, "订单中心", "订单管理", "订单列表", "列表页", "订单列表", "订单编号、用户手机号、订单状态、下单时间", "订单编号、服务类目、用户、服务商、金额、支付状态、订单状态、下单时间", "查看详情、审核、导出", "用于管理订单查询、审核与追踪", "需要菜单权限；审核动作需审核权限", "审核通过/驳回/冻结", "订单实体、订单分页接口", "高", "待梳理", "产品/运营/开发", "这一行是示例，可按实际需求替换"],
    [2, "基础配置", "服务类目管理", "服务类目列表", "树形列表页", "类目树", "类目名称、状态", "类目名称、上级类目、排序、状态、图标、更新时间", "新增同级、新增子级、编辑、删除、启用、停用", "参考菜单管理，使用树形列表展示类目层级，支持展开收起和层级维护", "需要类目管理菜单权限；删除前需校验是否存在子类目或关联业务", "新增成功/编辑成功/删除失败/状态切换成功", "服务类目实体、类目树接口、类目新增修改接口", "高", "待确认", "", "这一行体现树形列表维护方式"],
    [3, "商家管理", "服务商审核", "服务商审核列表", "列表页", "审核队列", "申请时间、地区、审核状态", "申请人、服务类目、资质状态、提交时间、审核状态", "查看、通过、驳回", "用于审核服务商入驻资料", "审核员角色可见", "通过/驳回", "服务商审核 BO/VO、审核接口", "高", "待确认", "", ""],
    [4, "", "", "", "详情页", "基础信息区块", "不涉及", "用户信息、订单信息、支付信息、操作日志", "锁单、解锁、退款、申诉处理", "梳理详情页需要展示的信息块和操作按钮", "需结合状态机定义可操作按钮", "按状态显示不同按钮", "订单详情接口、日志接口", "中", "待梳理", "", ""],
    [5, "", "", "", "弹窗/抽屉", "审核弹窗", "不涉及", "审核结果、驳回原因、备注", "确认提交", "说明弹窗字段与校验规则", "需填写驳回原因", "提交成功/失败", "审核提交接口", "中", "待梳理", "", ""],
  ];
  sheet.getRange("A11:Q15").values = sampleRows;
  formatBody(sheet, "A11:Q120");
  setPlaceholder(sheet, "B16:Q120");
  sheet.getRange("A16:A120").values = Array.from({ length: 105 }, (_, i) => [i + 6]);
  sheet.getRange("A15:A120").format = {
    font: { name: "Microsoft YaHei", size: 10, color: "#475569" },
    horizontalAlignment: "center",
    borders: { preset: "all", style: "thin", color: colors.border },
  };

  applyListValidation(sheet, "E11:E120", ["列表页", "树形列表页", "详情页", "表单页", "弹窗/抽屉", "配置页", "看板页", "其他"]);
  applyListValidation(sheet, "N11:N120", ["高", "中", "低"]);
  applyListValidation(sheet, "O11:O120", ["待梳理", "待确认", "已梳理"]);
  addStatusHighlight(sheet.getRange("N11:N120"));
  addLifeCycleHighlight(sheet.getRange("O11:O120"));

  sheet.getRange("A10:Q120").format.borders = { preset: "all", style: "thin", color: colors.border };
  sheet.getRange("A10:Q120").format.horizontalAlignment = "left";
  sheet.getRange("A10:A120").format.horizontalAlignment = "center";
  sheet.getRange("N10:O120").format.horizontalAlignment = "center";

  setWidths(sheet, {
    A: 8,
    B: 16,
    C: 16,
    D: 18,
    E: 12,
    F: 16,
    G: 22,
    H: 26,
    I: 20,
    J: 24,
    K: 20,
    L: 16,
    M: 20,
    N: 10,
    O: 10,
    P: 14,
    Q: 18,
  });

  const table = sheet.tables.add("A10:Q120", true, "AdminFunctionTemplate");
  table.style = "TableStyleMedium2";
  table.showFilterButton = true;
  applyCommonSheetSetup(sheet, "A1:Q120", 10);
  return sheet;
}

function buildMobileSheet() {
  const sheet = workbook.worksheets.add("移动端功能");
  setTitle(
    sheet,
    "移动端页面流梳理模板",
    "用途：以用户真实操作路径为主线梳理页面流。建议一行描述一个页面节点或关键状态节点，写清展示数据、按钮动作和跳转逻辑。",
    "T",
  );

  setIntro(sheet, 4, "T", [
    ["填写建议", "1. 以“用户从哪里来、在本页看什么、点什么、去哪里”为核心。"],
    ["填写建议", "2. 对同一页面的不同角色、不同状态、不同入口，建议拆成多行。"],
    ["填写建议", "3. 展示字段应写业务口径，不只是字段名；按钮应写触发条件和结果。"],
    ["填写建议", "4. 若页面依赖登录、定位、实名认证、支付状态等前置条件，请明确写出。"],
    ["交付口径", "梳理完成后，应能直接支撑原型、路由、接口、状态机、联调清单。"]],
  );

  setSection(sheet, "A10:T10", "页面流清单");
  const headers = [
    "流程编号",
    "业务流程名称",
    "步骤序号",
    "当前页面",
    "页面类型",
    "进入场景/入口",
    "上一个页面",
    "展示的数据说明",
    "核心字段/卡片",
    "主按钮",
    "次按钮/辅助动作",
    "按钮触发条件",
    "跳转去向",
    "跳转逻辑说明",
    "异常/空态/未登录处理",
    "前置条件",
    "相关接口/数据对象",
    "角色/身份",
    "优先级",
    "备注",
  ];
  setHeader(sheet, "A11:T11", headers);

  const sampleRows = [
    ["APP-001", "用户下单流程", 1, "首页", "首页", "App 启动后默认进入", "启动页", "展示服务类目、附近服务、活动位、消息提醒等首页聚合数据", "顶部定位、搜索、服务分类、活动 Banner、推荐服务卡片", "选择服务", "切换定位、搜索、查看消息", "已获取定位且服务可用", "服务详情页/需求发布页", "根据点击服务卡片或分类跳转对应业务页面", "定位失败提示授权；未登录可浏览但部分动作需登录", "定位权限、基础配置已加载", "首页聚合接口、活动接口、推荐服务接口", "普通用户", "高", "示例行，可复制扩展"],
    ["APP-001", "用户下单流程", 2, "服务详情页", "详情页", "从首页服务卡片进入", "首页", "展示服务详情、价格说明、服务范围、评价、服务商信息", "服务主图、价格、可预约时间、评价摘要、服务说明", "立即下单", "收藏、联系、分享", "服务可售且用户满足下单条件", "确认下单页", "点击立即下单，带上服务 ID 与默认规格进入确认页", "无库存/超范围时需提示并禁止下单", "登录、定位在服务范围内", "服务详情接口、收藏接口", "普通用户", "高", ""],
    ["APP-001", "用户下单流程", 3, "确认下单页", "表单页", "从服务详情页进入", "服务详情页", "展示收货地址/服务地址、服务时间、费用明细、优惠、支付方式", "地址卡片、时间选择、费用明细、优惠券、支付方式", "提交订单", "改地址、选时间、选券", "表单校验通过且支付方式可用", "支付收银台/订单详情", "提交成功后按是否需要支付决定进入收银台或订单详情", "地址缺失、时间不可选、优惠失效都要提示", "登录、已完善必要资料", "订单创建接口、优惠券接口、地址接口", "普通用户", "高", ""],
    ["APP-002", "服务商接单流程", 1, "需求订单列表", "列表页", "服务商工作台进入", "服务商工作台", "展示待接单、进行中、已完成等订单分组列表", "状态切换 Tab、订单卡片、距离、报价、时间", "查看详情", "筛选、刷新、切换状态", "服务商已认证且有接单权限", "订单详情页", "根据订单状态展示不同入口和筛选条件", "无数据时展示空态引导；无权限时展示认证引导", "服务商身份、定位权限", "订单列表接口、服务商状态接口", "服务商", "高", ""],
  ];
  sheet.getRange("A12:T15").values = sampleRows;
  formatBody(sheet, "A12:T140");
  setPlaceholder(sheet, "A16:T140");

  applyListValidation(sheet, "E12:E140", ["首页", "列表页", "详情页", "表单页", "弹窗", "结果页", "个人中心页", "其他"]);
  applyListValidation(sheet, "R12:R140", ["普通用户", "服务商", "区域合作商", "游客", "通用"]);
  applyListValidation(sheet, "S12:S140", ["高", "中", "低"]);
  addStatusHighlight(sheet.getRange("S12:S140"));

  sheet.getRange("C12:C140").format.horizontalAlignment = "center";
  sheet.getRange("R12:S140").format.horizontalAlignment = "center";

  setWidths(sheet, {
    A: 12,
    B: 18,
    C: 9,
    D: 16,
    E: 12,
    F: 18,
    G: 14,
    H: 26,
    I: 24,
    J: 16,
    K: 18,
    L: 18,
    M: 16,
    N: 24,
    O: 22,
    P: 18,
    Q: 20,
    R: 12,
    S: 10,
    T: 18,
  });

  const table = sheet.tables.add("A11:T140", true, "MobileFlowTemplate");
  table.style = "TableStyleMedium9";
  table.showFilterButton = true;
  applyCommonSheetSetup(sheet, "A1:T140", 11);
  return sheet;
}

function buildPcSheet() {
  const sheet = workbook.worksheets.add("PC端功能");
  setTitle(
    sheet,
    "PC端功能梳理模板",
    "用途：当前阶段先做轻量占位。后续若 PC 端需求增多，可沿用管理端或移动端思路继续扩展字段。",
    "L",
  );

  setIntro(sheet, 4, "L", [
    ["当前策略", "PC 端暂不作为当前优先建设端，本页签用于预留未来需求梳理。"],
    ["建议做法", "若后续 PC 端以运营后台为主，可复制管理端梳理方式；若以门户/商家工作台为主，可复制移动端页面流方式。"],
    ["最少字段", "建议先记录页面名称、目标用户、核心场景、展示内容、操作按钮、跳转逻辑。"],
    ["交付口径", "当 PC 端开始排期前，先补齐页面流、权限、接口和异常处理，再进入开发。"]],
  );

  setSection(sheet, "A9:L9", "预留清单");
  const headers = [
    "序号",
    "模块/页面",
    "目标用户",
    "核心场景",
    "展示的数据说明",
    "主按钮",
    "次按钮",
    "跳转逻辑",
    "相关接口/数据对象",
    "优先级",
    "梳理状态",
    "备注",
  ];
  setHeader(sheet, "A10:L10", headers);
  const sampleRows = [
    [1, "商家工作台首页", "服务商", "查看经营概览、待处理事项", "展示订单统计、收入、待审核事项、快捷入口", "查看待办", "筛选时间", "点击数据卡片进入对应列表页", "工作台统计接口", "低", "待梳理", "占位示例"],
    [2, "", "", "", "", "", "", "", "", "", "", ""],
    [3, "", "", "", "", "", "", "", "", "", "", ""],
  ];
  sheet.getRange("A11:L13").values = sampleRows;
  formatBody(sheet, "A11:L80");
  setPlaceholder(sheet, "B12:L80");
  sheet.getRange("A14:A80").values = Array.from({ length: 67 }, (_, i) => [i + 4]);
  sheet.getRange("A14:A80").format = {
    font: { name: "Microsoft YaHei", size: 10, color: "#475569" },
    horizontalAlignment: "center",
    borders: { preset: "all", style: "thin", color: colors.border },
  };

  applyListValidation(sheet, "J11:J80", ["高", "中", "低"]);
  applyListValidation(sheet, "K11:K80", ["待梳理", "待确认", "已梳理"]);
  addStatusHighlight(sheet.getRange("J11:J80"));
  addLifeCycleHighlight(sheet.getRange("K11:K80"));

  setWidths(sheet, {
    A: 8,
    B: 18,
    C: 12,
    D: 20,
    E: 24,
    F: 16,
    G: 16,
    H: 20,
    I: 18,
    J: 10,
    K: 10,
    L: 18,
  });

  const table = sheet.tables.add("A10:L80", true, "PcFunctionTemplate");
  table.style = "TableStyleMedium4";
  table.showFilterButton = true;
  applyCommonSheetSetup(sheet, "A1:L80", 10);
  return sheet;
}

buildAdminSheet();
buildMobileSheet();
buildPcSheet();

const formulaErrors = await workbook.inspect({
  kind: "match",
  searchTerm: "#REF!|#DIV/0!|#VALUE!|#NAME\\?|#N/A",
  options: { useRegex: true, maxResults: 200 },
  summary: "final formula error scan",
});

const adminPreview = await workbook.render({
  sheetName: "管理端功能",
  range: "A1:Q24",
  scale: 1.5,
  format: "png",
});
await fs.writeFile(path.join(previewDir, "admin.png"), new Uint8Array(await adminPreview.arrayBuffer()));

const mobilePreview = await workbook.render({
  sheetName: "移动端功能",
  range: "A1:T24",
  scale: 1.3,
  format: "png",
});
await fs.writeFile(path.join(previewDir, "mobile.png"), new Uint8Array(await mobilePreview.arrayBuffer()));

const pcPreview = await workbook.render({
  sheetName: "PC端功能",
  range: "A1:L20",
  scale: 1.5,
  format: "png",
});
await fs.writeFile(path.join(previewDir, "pc.png"), new Uint8Array(await pcPreview.arrayBuffer()));

const xlsx = await SpreadsheetFile.exportXlsx(workbook);
await xlsx.save(outputPath);

console.log(JSON.stringify({
  outputPath,
  previewDir,
  formulaErrors: formulaErrors.ndjson,
}));
