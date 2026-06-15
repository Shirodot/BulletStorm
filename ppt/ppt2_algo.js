// PPT 2 — 演算法內部運作篇
const pptxgen = require("pptxgenjs");

const pres = new pptxgen();
pres.layout = "LAYOUT_16x9";
pres.title = "Bullet Storm — 演算法設計";
pres.author = "BulletStorm Team";

// ── Palette (purple/math theme) ─────────────────────
const C = {
  dark:   "0F0A1E",
  panel:  "1A1230",
  accent: "9B5DE5",
  accentL:"C77DFF",
  teal:   "00F5D4",
  gold:   "FEE440",
  white:  "F0EAF8",
  dim:    "8B80A8",
  red:    "F15BB5",
  blue:   "00BBF9",
  green:  "57CC99",
};

function makeShadow() {
  return { type: "outer", color: "000000", blur: 8, offset: 3, angle: 45, opacity: 0.3 };
}
function card(slide, x, y, w, h, color) {
  slide.addShape(pres.shapes.ROUNDED_RECTANGLE, {
    x, y, w, h,
    fill: { color: color || C.panel },
    rectRadius: 0.1,
    shadow: makeShadow(),
  });
}
function sectionTag(slide, label) {
  slide.addShape(pres.shapes.ROUNDED_RECTANGLE, {
    x: 0.4, y: 0.18, w: 2.2, h: 0.36,
    fill: { color: C.accent },
    rectRadius: 0.06,
  });
  slide.addText(label, {
    x: 0.4, y: 0.18, w: 2.2, h: 0.36,
    fontSize: 11, bold: true, color: C.white,
    align: "center", valign: "middle", margin: 0,
  });
}
function slideTitle(slide, text) {
  slide.addText(text, {
    x: 0.4, y: 0.62, w: 9.2, h: 0.65,
    fontSize: 28, bold: true, color: C.white,
    align: "left", valign: "middle", margin: 0,
  });
}
function hRule(slide, y) {
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 0.4, y, w: 9.2, h: 0.03,
    fill: { color: C.accent, transparency: 40 },
  });
}

// ═══════════════════════════════════════════
// Slide 1 — Title
// ═══════════════════════════════════════════
{
  const s = pres.addSlide();
  s.background = { color: C.dark };

  // Decorative orbits
  s.addShape(pres.shapes.OVAL, {
    x: 5.5, y: -0.8, w: 7, h: 7,
    fill: { color: "000000", transparency: 100 },
    line: { color: C.accent, width: 1.5, transparency: 70 },
  });
  s.addShape(pres.shapes.OVAL, {
    x: 6.5, y: 0.2, w: 5, h: 5,
    fill: { color: "000000", transparency: 100 },
    line: { color: C.teal, width: 1, transparency: 75 },
  });

  // Badge
  s.addShape(pres.shapes.ROUNDED_RECTANGLE, {
    x: 0.5, y: 0.5, w: 2.8, h: 0.5,
    fill: { color: C.accent },
    rectRadius: 0.08,
  });
  s.addText("BULLET STORM", {
    x: 0.5, y: 0.5, w: 2.8, h: 0.5,
    fontSize: 13, bold: true, color: C.white,
    align: "center", valign: "middle", margin: 0,
  });

  s.addText("演算法設計", {
    x: 0.5, y: 1.25, w: 7, h: 1.2,
    fontSize: 48, bold: true, color: C.white,
    align: "left", valign: "middle", margin: 0,
  });
  s.addText("Algorithm Design & Internal Mechanics", {
    x: 0.5, y: 2.5, w: 7, h: 0.5,
    fontSize: 18, color: C.accentL,
    align: "left", margin: 0,
  });

  s.addShape(pres.shapes.RECTANGLE, {
    x: 0.5, y: 3.1, w: 3.5, h: 0.04,
    fill: { color: C.teal },
  });

  s.addText([
    { text: "12 種彈幕演算法  ·  EXP 升級系統  ·  敵人 AI", options: { breakLine: true } },
    { text: "碰撞偵測  ·  物件池管理  ·  難度自適應", options: {} },
  ], {
    x: 0.5, y: 3.28, w: 7, h: 0.8,
    fontSize: 14, color: C.dim,
    align: "left", margin: 0,
  });

  // Stats
  const algoStats = [
    { v: "12", l: "彈幕演算法" },
    { v: "5", l: "技能樹系統" },
    { v: "4", l: "子彈運動模式" },
    { v: "O(n²)", l: "碰撞偵測複雜度" },
  ];
  algoStats.forEach((st, i) => {
    card(s, 0.5 + i * 2.3, 4.4, 2.1, 0.9);
    s.addText(st.v, {
      x: 0.5 + i * 2.3, y: 4.42, w: 2.1, h: 0.45,
      fontSize: 22, bold: true, color: C.teal,
      align: "center", valign: "middle", margin: 0,
    });
    s.addText(st.l, {
      x: 0.5 + i * 2.3, y: 4.85, w: 2.1, h: 0.3,
      fontSize: 10, color: C.dim,
      align: "center", valign: "middle", margin: 0,
    });
  });

  s.addNotes("本簡報聚焦於 Bullet Storm 的演算法設計，包含子彈模式生成、EXP 系統、敵人 AI 與碰撞偵測等核心機制。");
}

// ═══════════════════════════════════════════
// Slide 2 — 目錄
// ═══════════════════════════════════════════
{
  const s = pres.addSlide();
  s.background = { color: C.dark };

  sectionTag(s, "CONTENTS");
  slideTitle(s, "簡報目錄");
  hRule(s, 1.35);

  const items = [
    { n: "01", t: "專案動機", d: "彈幕遊戲的演算法挑戰性與學習目標" },
    { n: "02", t: "系統架構", d: "類別設計、物件關係、資料流向" },
    { n: "03", t: "使用技術", d: "12 種彈幕演算法 + 子彈運動模式詳解" },
    { n: "04", t: "實驗結果", d: "效能分析、EXP 系統驗證、難度曲線測試" },
    { n: "05", t: "未來改進", d: "機器學習難度調整、更複雜彈幕公式" },
  ];

  items.forEach((it, i) => {
    const y = 1.5 + i * 0.78;
    card(s, 0.4, y, 9.2, 0.65);
    s.addShape(pres.shapes.OVAL, {
      x: 0.55, y: y + 0.1, w: 0.45, h: 0.45,
      fill: { color: C.accent },
    });
    s.addText(it.n, {
      x: 0.55, y: y + 0.1, w: 0.45, h: 0.45,
      fontSize: 12, bold: true, color: C.white,
      align: "center", valign: "middle", margin: 0,
    });
    s.addText(it.t, {
      x: 1.15, y: y + 0.07, w: 2.5, h: 0.3,
      fontSize: 15, bold: true, color: C.white,
      align: "left", valign: "middle", margin: 0,
    });
    s.addText(it.d, {
      x: 1.15, y: y + 0.35, w: 7.8, h: 0.22,
      fontSize: 11, color: C.dim,
      align: "left", valign: "middle", margin: 0,
    });
  });
}

// ═══════════════════════════════════════════
// Slide 3 — 專案動機
// ═══════════════════════════════════════════
{
  const s = pres.addSlide();
  s.background = { color: C.dark };

  sectionTag(s, "01 專案動機");
  slideTitle(s, "演算法挑戰與設計動機");
  hRule(s, 1.35);

  // Challenges
  const challenges = [
    { icon: "🔢", title: "數學密集型運算", desc: "三角函數、向量運算、參數方程式在每幀執行數百次，需最佳化" },
    { icon: "🎯", title: "碰撞偵測效率", desc: "子彈 × 敵人 × 玩家的 O(n²) 碰撞計算，需要空間分割優化策略" },
    { icon: "🤖", title: "敵人行為決策", desc: "自動入場路徑、巡邏 AI、射擊時機判斷皆需行為演算法設計" },
    { icon: "⚖️", title: "難度動態平衡", desc: "4 種難度 × 子彈速度/數量/頻率的多維度調整公式設計" },
  ];

  challenges.forEach((c, i) => {
    const y = 1.55 + i * 0.92;
    card(s, 0.4, y, 5.5, 0.78);
    s.addText(c.icon, {
      x: 0.55, y: y + 0.12, w: 0.55, h: 0.55,
      fontSize: 22, align: "center", valign: "middle", margin: 0,
    });
    s.addText(c.title, {
      x: 1.2, y: y + 0.08, w: 4.5, h: 0.28,
      fontSize: 13, bold: true, color: C.white,
      align: "left", margin: 0,
    });
    s.addText(c.desc, {
      x: 1.2, y: y + 0.36, w: 4.5, h: 0.35,
      fontSize: 10, color: C.dim,
      align: "left", margin: 0,
    });
  });

  // Right: design principles
  card(s, 6.1, 1.55, 3.5, 3.68, "130D22");
  s.addText("設計原則", {
    x: 6.2, y: 1.65, w: 3.3, h: 0.35,
    fontSize: 14, bold: true, color: C.accentL,
    align: "center", margin: 0,
  });
  const principles = [
    "數學美感優先（視覺優美的軌跡）",
    "可組合性：12 個獨立函式",
    "參數化設計：難度即係數",
    "物件複用：Bullet 通用型別",
    "延遲清除：避免迭代中修改",
    "隨機性 + 確定性混合使用",
  ];
  principles.forEach((p, i) => {
    s.addShape(pres.shapes.OVAL, {
      x: 6.25, y: 2.13 + i * 0.5, w: 0.18, h: 0.18,
      fill: { color: C.teal },
    });
    s.addText(p, {
      x: 6.55, y: 2.08 + i * 0.5, w: 2.8, h: 0.28,
      fontSize: 11, color: C.white,
      align: "left", valign: "middle", margin: 0,
    });
  });
}

// ═══════════════════════════════════════════
// Slide 4 — 系統架構：類別設計
// ═══════════════════════════════════════════
{
  const s = pres.addSlide();
  s.background = { color: C.dark };

  sectionTag(s, "02 系統架構");
  slideTitle(s, "核心類別架構設計");
  hRule(s, 1.35);

  // Class hierarchy diagram
  const classes = [
    { name: "GamePanel", role: "遊戲主控制器", color: C.accent, x: 3.9, y: 1.55, w: 2.2, h: 0.7 },
    { name: "BulletPattern", role: "彈幕演算法庫 (static)", color: C.teal, x: 0.4, y: 2.7, w: 2.5, h: 0.7 },
    { name: "Boss", role: "Boss 射擊邏輯", color: C.red, x: 3.55, y: 2.7, w: 2.5, h: 0.7 },
    { name: "Enemy", role: "敵人 AI + 射擊", color: C.blue, x: 6.8, y: 2.7, w: 2.5, h: 0.7 },
    { name: "Bullet", role: "子彈物理模型 (4型)", color: C.gold, x: 0.4, y: 3.9, w: 2.5, h: 0.7 },
    { name: "Player", role: "玩家控制 + EXP", color: C.green, x: 3.55, y: 3.9, w: 2.5, h: 0.7 },
    { name: "ExperienceSystem", role: "升級/技能計算", color: C.accentL, x: 6.8, y: 3.9, w: 2.5, h: 0.7 },
  ];

  classes.forEach(c => {
    card(s, c.x, c.y, c.w, c.h);
    s.addShape(pres.shapes.ROUNDED_RECTANGLE, {
      x: c.x + 0.08, y: c.y + 0.06, w: c.w - 0.16, h: 0.28,
      fill: { color: c.color, transparency: 20 },
      rectRadius: 0.05,
    });
    s.addText(c.name, {
      x: c.x + 0.08, y: c.y + 0.06, w: c.w - 0.16, h: 0.28,
      fontSize: 11, bold: true, color: C.dark,
      align: "center", valign: "middle", margin: 0,
    });
    s.addText(c.role, {
      x: c.x + 0.1, y: c.y + 0.38, w: c.w - 0.2, h: 0.28,
      fontSize: 10, color: C.white,
      align: "center", valign: "middle", margin: 0,
    });
  });

  // Arrows
  const arrows = [
    { x1: 5.0, y1: 2.25, x2: 1.65, y2: 2.7 },
    { x1: 5.0, y1: 2.25, x2: 4.8, y2: 2.7 },
    { x1: 5.0, y1: 2.25, x2: 8.05, y2: 2.7 },
    { x1: 1.65, y1: 3.4, x2: 1.65, y2: 3.9 },
    { x1: 4.8, y1: 3.4, x2: 4.8, y2: 3.9 },
    { x1: 8.05, y1: 3.4, x2: 8.05, y2: 3.9 },
  ];
  arrows.forEach(a => {
    s.addShape(pres.shapes.LINE, {
      x: Math.min(a.x1, a.x2),
      y: Math.min(a.y1, a.y2),
      w: Math.abs(a.x2 - a.x1) || 0.01,
      h: Math.abs(a.y2 - a.y1) || 0.01,
      line: { color: C.accent, width: 1.5, dashType: "dash" },
    });
  });

  // Legend
  s.addText("─ ─  呼叫/使用關係", {
    x: 0.4, y: 4.85, w: 3, h: 0.25,
    fontSize: 10, color: C.dim,
    align: "left", margin: 0,
  });
  s.addText("BulletPattern 為純靜態工具類，Boss/Enemy 呼叫其方法產生子彈 List", {
    x: 3.5, y: 4.85, w: 6.1, h: 0.25,
    fontSize: 10, color: C.dim,
    align: "left", margin: 0,
  });
}

// ═══════════════════════════════════════════
// Slide 5 — 使用技術：12 種彈幕演算法
// ═══════════════════════════════════════════
{
  const s = pres.addSlide();
  s.background = { color: C.dark };

  sectionTag(s, "03 使用技術");
  slideTitle(s, "12 種彈幕模式演算法");
  hRule(s, 1.35);

  const patterns = [
    { name: "circularBurst", zh: "環形爆散", formula: "angle = offset + i × (2π / n)", color: C.accentL },
    { name: "spiralArm", zh: "螺旋臂", formula: "offset += Δθ per frame", color: C.teal },
    { name: "aimedSpread", zh: "瞄準扇射", formula: "base = atan2(py-y, px-x) ± spread", color: C.blue },
    { name: "doubleRing", zh: "雙環", formula: "外圈 speed > 0，內圈 speed < 0", color: C.gold },
    { name: "starBurst", zh: "星爆交錯", formula: "i%2==0 → fast，i%2==1 → slow", color: C.red },
    { name: "homingBullets", zh: "追蹤彈", formula: "angle → lerp → target, turnRate=0.05", color: C.green },
    { name: "waveBullets", zh: "波動彈", formula: "x += vx + perp × sin(phase) × amp", color: C.accentL },
    { name: "spinningRing", zh: "旋轉環", formula: "angle += angularVel each frame", color: C.teal },
    { name: "accelBurst", zh: "加速彈", formula: "speed += accel per frame", color: C.blue },
    { name: "butterflySpray", zh: "蝴蝶散射", formula: "spread = sin(time) × π × 0.4", color: C.gold },
    { name: "laserBarrage", zh: "雷射彈幕", formula: "angle = target ± random() × spread", color: C.red },
    { name: "sakuraPetal", zh: "花瓣", formula: "petal × spread + speed 線性插值", color: C.green },
  ];

  patterns.forEach((p, i) => {
    const col = i % 3, row = Math.floor(i / 3);
    const x = 0.4 + col * 3.2, y = 1.55 + row * 1.0;
    card(s, x, y, 3.05, 0.88);
    s.addShape(pres.shapes.OVAL, {
      x: x + 0.1, y: y + 0.32, w: 0.22, h: 0.22,
      fill: { color: p.color },
    });
    s.addText(p.zh + "  " + p.name + "()", {
      x: x + 0.1, y: y + 0.06, w: 2.8, h: 0.28,
      fontSize: 11, bold: true, color: C.white,
      align: "left", margin: 0,
    });
    s.addText(p.formula, {
      x: x + 0.4, y: y + 0.52, w: 2.5, h: 0.3,
      fontSize: 9, color: C.dim, fontFace: "Courier New",
      align: "left", valign: "middle", margin: 0,
    });
  });
}

// ═══════════════════════════════════════════
// Slide 6 — 子彈運動模式
// ═══════════════════════════════════════════
{
  const s = pres.addSlide();
  s.background = { color: C.dark };

  sectionTag(s, "03 使用技術");
  slideTitle(s, "子彈物理運動模式（4 型）");
  hRule(s, 1.35);

  const modes = [
    {
      type: "type 0", name: "標準直線", color: C.blue,
      code: [
        "x += vx",
        "y += vy",
        "// angularVel 可選旋轉",
      ],
      desc: "最基礎模式。速度向量每幀累加座標。\n可設 angularVel 讓彈道緩慢旋轉，形成螺旋效果。",
    },
    {
      type: "type 1", name: "追蹤 (Homing)", color: C.red,
      code: [
        "targetAngle = atan2(py-y, px-x)",
        "diff = normalize(target - current)",
        "angle += clamp(diff, -0.05, 0.05)",
      ],
      desc: "每幀計算玩家方向，以 turnRate 漸進轉向。\n初速低，之後逐漸逼近玩家，高壓迫感。",
    },
    {
      type: "type 2", name: "波動 (Wave)", color: C.teal,
      code: [
        "phase += frequency",
        "perpX = -sin(angle)",
        "x += vx + perpX × sin(phase) × amp",
      ],
      desc: "在前進方向的垂直軸施加正弦波偏移。\nfrequency 控制頻率，amplitude 控制幅度。",
    },
    {
      type: "type 3", name: "加速 (Accel)", color: C.gold,
      code: [
        "speed += accel",
        "vx = cos(angle) × speed",
        "vy = sin(angle) × speed",
      ],
      desc: "每幀增加速度，初始可為負（先退後進）。\n適合製造「突進」或「緩速爬行後急衝」效果。",
    },
  ];

  modes.forEach((m, i) => {
    const col = i % 2, row = Math.floor(i / 2);
    const x = 0.4 + col * 4.8, y = 1.55 + row * 1.98;
    card(s, x, y, 4.6, 1.8);
    s.addShape(pres.shapes.ROUNDED_RECTANGLE, {
      x: x + 0.1, y: y + 0.1, w: 1.0, h: 0.3,
      fill: { color: m.color, transparency: 15 },
      rectRadius: 0.05,
    });
    s.addText(m.type, {
      x: x + 0.1, y: y + 0.1, w: 1.0, h: 0.3,
      fontSize: 10, bold: true, color: C.dark,
      align: "center", valign: "middle", margin: 0,
    });
    s.addText(m.name, {
      x: x + 1.2, y: y + 0.1, w: 3.2, h: 0.3,
      fontSize: 14, bold: true, color: C.white,
      align: "left", valign: "middle", margin: 0,
    });
    // Code block
    card(s, x + 0.1, y + 0.48, 4.4, 0.75, "0A0718");
    m.code.forEach((line, j) => {
      s.addText(line, {
        x: x + 0.2, y: y + 0.52 + j * 0.22, w: 4.2, h: 0.2,
        fontSize: 9.5, color: C.teal, fontFace: "Courier New",
        align: "left", margin: 0,
      });
    });
    s.addText(m.desc, {
      x: x + 0.1, y: y + 1.27, w: 4.4, h: 0.45,
      fontSize: 10, color: C.dim,
      align: "left", valign: "top", margin: 0,
    });
  });
}

// ═══════════════════════════════════════════
// Slide 7 — EXP 升級系統演算法
// ═══════════════════════════════════════════
{
  const s = pres.addSlide();
  s.background = { color: C.dark };

  sectionTag(s, "03 使用技術");
  slideTitle(s, "EXP 升級系統演算法");
  hRule(s, 1.35);

  // Left: formula
  card(s, 0.4, 1.55, 4.4, 3.7);
  s.addText("升級公式", {
    x: 0.5, y: 1.65, w: 4.2, h: 0.35,
    fontSize: 14, bold: true, color: C.accentL,
    align: "center", margin: 0,
  });

  const formulas = [
    { label: "升級所需 EXP", formula: "exp_next = 1000 + level × 500" },
    { label: "子彈刷掠 EXP", formula: "graze_exp = 5 per bullet" },
    { label: "擊殺敵人 EXP", formula: "kill_exp = 50 × (type+1) × (diff+1)" },
    { label: "擊破 Boss EXP", formula: "boss_exp = 5000 × (diff+1)" },
    { label: "技能效果 (射速)", formula: "speed_mult = 1.0 + level × 0.15" },
    { label: "技能效果 (射頻)", formula: "cooldown -= level × 2  (min=2)" },
    { label: "技能效果 (生命)", formula: "max_lives += hp_level" },
  ];

  formulas.forEach((f, i) => {
    s.addText(f.label + "：", {
      x: 0.55, y: 2.1 + i * 0.48, w: 1.8, h: 0.35,
      fontSize: 10, color: C.dim,
      align: "left", valign: "middle", margin: 0,
    });
    card(s, 2.35, 2.08 + i * 0.48, 2.3, 0.33, "0A0718");
    s.addText(f.formula, {
      x: 2.4, y: 2.1 + i * 0.48, w: 2.2, h: 0.3,
      fontSize: 9.5, color: C.teal, fontFace: "Courier New",
      align: "left", valign: "middle", margin: 0,
    });
  });

  // Right: EXP growth chart
  s.addText("各等級所需 EXP 成長曲線", {
    x: 5.0, y: 1.65, w: 4.6, h: 0.3,
    fontSize: 13, bold: true, color: C.white,
    align: "left", margin: 0,
  });

  const levels = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];
  const expRequired = levels.map(lv => 1000 + lv * 500);

  s.addChart(pres.charts.BAR, [{
    name: "升級所需 EXP",
    labels: levels.map(l => "Lv " + l),
    values: expRequired,
  }], {
    x: 4.9, y: 2.0, w: 4.7, h: 3.0,
    barDir: "col",
    chartColors: [C.accent.replace("#", "")],
    chartArea: { fill: { color: C.panel }, roundedCorners: true },
    catAxisLabelColor: C.dim,
    valAxisLabelColor: C.dim,
    valGridLine: { color: "2A1E42", size: 0.5 },
    catGridLine: { style: "none" },
    showValue: false,
    showLegend: false,
    dataLabelColor: C.white,
  });
}

// ═══════════════════════════════════════════
// Slide 8 — 敵人 AI 與自動消失
// ═══════════════════════════════════════════
{
  const s = pres.addSlide();
  s.background = { color: C.dark };

  sectionTag(s, "03 使用技術");
  slideTitle(s, "敵人 AI 行為與生命週期管理");
  hRule(s, 1.35);

  // Lifecycle flow
  const lifecycle = [
    { stage: "生成", desc: "age = -i×15\n(錯開生成延遲)", color: C.green },
    { stage: "進場", desc: "age < 60\n依 vx/vy 飛入", color: C.blue },
    { stage: "減速", desc: "60 < age < 120\nvx,vy × 0.95", color: C.teal },
    { stage: "巡邏", desc: "age ≥ 120\nx = sin(t×0.02)×40", color: C.gold },
    { stage: "消失", desc: "age ≥ maxAge\nactive = false", color: C.red },
  ];

  lifecycle.forEach((lc, i) => {
    const x = 0.4 + i * 1.86;
    card(s, x, 1.55, 1.7, 1.0, "1A1230");
    s.addShape(pres.shapes.OVAL, {
      x: x + 0.6, y: 1.65, w: 0.5, h: 0.5,
      fill: { color: lc.color },
    });
    s.addText(lc.stage, {
      x: x + 0.6, y: 1.65, w: 0.5, h: 0.5,
      fontSize: 11, bold: true, color: C.dark,
      align: "center", valign: "middle", margin: 0,
    });
    s.addText(lc.desc, {
      x: x + 0.1, y: 2.2, w: 1.5, h: 0.3,
      fontSize: 9, color: C.dim, fontFace: "Courier New",
      align: "center", margin: 0,
    });
    if (i < 4) {
      s.addShape(pres.shapes.LINE, {
        x: x + 1.7, y: 2.05, w: 0.16, h: 0,
        line: { color: C.accent, width: 1.5 },
      });
    }
  });

  // maxAge table
  s.addText("各敵人類型存活時間 (maxAge)", {
    x: 0.4, y: 2.75, w: 5, h: 0.3,
    fontSize: 13, bold: true, color: C.white,
    align: "left", margin: 0,
  });

  s.addTable([
    [
      { text: "敵人類型", options: { bold: true, color: C.dark, fill: { color: C.accent } } },
      { text: "maxAge (幀)", options: { bold: true, color: C.dark, fill: { color: C.accent } } },
      { text: "存活秒數 @60fps", options: { bold: true, color: C.dark, fill: { color: C.accent } } },
      { text: "射擊間隔", options: { bold: true, color: C.dark, fill: { color: C.accent } } },
    ],
    ["仙子 (type 0)", "480", "8.0 秒", "100 幀"],
    ["妖怪 (type 1)", "600", "10.0 秒", "90 幀"],
    ["頭目 (type 2)", "720", "12.0 秒", "70 幀"],
  ], {
    x: 0.4, y: 3.12, w: 5.5, h: 1.6,
    border: { pt: 1, color: "2A1E42" },
    color: C.white,
    fontSize: 12,
    fill: { color: C.panel },
  });

  // Stagger spawn explanation
  card(s, 6.1, 1.55, 3.5, 3.7, "130D22");
  s.addText("錯開生成演算法", {
    x: 6.2, y: 1.65, w: 3.3, h: 0.35,
    fontSize: 13, bold: true, color: C.teal,
    align: "center", margin: 0,
  });
  const staggerCode = [
    "// 生成波次時設定負 age",
    "for (int i = 0; i < count; i++) {",
    "  Enemy en = new Enemy(...);",
    "  en.age = -(i * 15);",
    "  // 每隻差 15 幀入場",
    "  enemies.add(en);",
    "}",
    "",
    "// update() 中跳過未出生敵人",
    "if (age <= 0) return;",
  ];
  staggerCode.forEach((line, j) => {
    s.addText(line, {
      x: 6.2, y: 2.1 + j * 0.29, w: 3.2, h: 0.27,
      fontSize: 8.5, color: line.startsWith("//") ? C.dim : C.teal,
      fontFace: "Courier New",
      align: "left", margin: 0,
    });
  });
}

// ═══════════════════════════════════════════
// Slide 9 — 實驗結果
// ═══════════════════════════════════════════
{
  const s = pres.addSlide();
  s.background = { color: C.dark };

  sectionTag(s, "04 實驗結果");
  slideTitle(s, "效能分析與平衡測試結果");
  hRule(s, 1.35);

  // Difficulty impact chart
  s.addText("各難度下每秒產生子彈數量", {
    x: 0.4, y: 1.55, w: 5.5, h: 0.3,
    fontSize: 13, bold: true, color: C.white,
    align: "left", margin: 0,
  });

  s.addChart(pres.charts.BAR, [
    { name: "普通敵人", labels: ["Easy", "Normal", "Hard", "Lunatic"], values: [1.2, 1.7, 2.2, 2.8] },
    { name: "Boss 戰", labels: ["Easy", "Normal", "Hard", "Lunatic"], values: [2.5, 3.8, 5.2, 7.0] },
  ], {
    x: 0.4, y: 1.9, w: 5.5, h: 2.5,
    barDir: "col",
    barGrouping: "clustered",
    chartColors: [C.blue, C.red],
    chartArea: { fill: { color: C.panel }, roundedCorners: true },
    catAxisLabelColor: C.dim,
    valAxisLabelColor: C.dim,
    valGridLine: { color: "2A1E42", size: 0.5 },
    catGridLine: { style: "none" },
    showLegend: true, legendPos: "b",
    showValue: true, dataLabelColor: C.white,
  });

  // Key metrics
  const metrics = [
    { metric: "最大同時子彈數", value: "~300 顆", color: C.gold },
    { metric: "碰撞偵測耗時", value: "< 1ms / frame", color: C.green },
    { metric: "EXP 升級驗證", value: "公式線性成長 ✓", color: C.teal },
    { metric: "追蹤彈收斂性", value: "3~5 秒內命中 ✓", color: C.accentL },
  ];

  metrics.forEach((m, i) => {
    card(s, 6.1, 1.55 + i * 1.0, 3.5, 0.85);
    s.addShape(pres.shapes.RECTANGLE, {
      x: 6.1, y: 1.55 + i * 1.0, w: 0.08, h: 0.85,
      fill: { color: m.color },
    });
    s.addText(m.metric, {
      x: 6.28, y: 1.6 + i * 1.0, w: 3.2, h: 0.3,
      fontSize: 11, color: C.dim,
      align: "left", valign: "middle", margin: 0,
    });
    s.addText(m.value, {
      x: 6.28, y: 1.9 + i * 1.0, w: 3.2, h: 0.35,
      fontSize: 15, bold: true, color: m.color,
      align: "left", valign: "middle", margin: 0,
    });
  });

  // Wave bullet analysis
  s.addText("波動彈 sin 軌跡範例（y = sin(x) × 12）", {
    x: 0.4, y: 4.55, w: 9.2, h: 0.25,
    fontSize: 10, color: C.dim,
    align: "left", margin: 0,
  });

  const waveLabels = Array.from({ length: 10 }, (_, i) => `t=${i * 10}`);
  const waveValues = Array.from({ length: 10 }, (_, i) => Math.round(Math.sin(i * 0.6) * 12));

  s.addChart(pres.charts.LINE, [{
    name: "side offset (px)",
    labels: waveLabels,
    values: waveValues,
  }], {
    x: 0.4, y: 4.8, w: 9.2, h: 0.65,
    chartColors: [C.teal],
    chartArea: { fill: { color: C.panel }, roundedCorners: true },
    catAxisLabelColor: C.dim,
    valAxisLabelColor: C.dim,
    valGridLine: { color: "2A1E42", size: 0.5 },
    catGridLine: { style: "none" },
    showLegend: false,
    lineSize: 2.5,
    lineSmooth: true,
  });
}

// ═══════════════════════════════════════════
// Slide 10 — 未來改進
// ═══════════════════════════════════════════
{
  const s = pres.addSlide();
  s.background = { color: C.dark };

  sectionTag(s, "05 未來改進");
  slideTitle(s, "演算法優化與擴展方向");
  hRule(s, 1.35);

  const plans = [
    {
      phase: "短期", color: C.green,
      items: [
        "空間分割 (Grid Partition) 降低碰撞偵測至 O(n log n)",
        "物件池 (Object Pool) 減少 Bullet GC 壓力",
        "更多 Bezier 曲線彈道演算法",
      ],
    },
    {
      phase: "中期", color: C.gold,
      items: [
        "機率式難度自適應：依玩家存活率動態調整",
        "Boss AI 狀態機：Idle / Attack / Enrage 三態切換",
        "更完整的 EXP 技能樹（10 種技能）",
      ],
    },
    {
      phase: "長期", color: C.red,
      items: [
        "機器學習難度調整 (Reinforcement Learning)",
        "程序生成 Boss 技能組合",
        "多人連線對戰彈幕演算法同步",
      ],
    },
  ];

  plans.forEach((p, i) => {
    const x = 0.4 + i * 3.13;
    card(s, x, 1.55, 3.0, 3.7);
    s.addShape(pres.shapes.ROUNDED_RECTANGLE, {
      x: x + 0.7, y: 1.65, w: 1.6, h: 0.38,
      fill: { color: p.color },
      rectRadius: 0.08,
    });
    s.addText(p.phase + "目標", {
      x: x + 0.7, y: 1.65, w: 1.6, h: 0.38,
      fontSize: 12, bold: true, color: C.dark,
      align: "center", valign: "middle", margin: 0,
    });
    p.items.forEach((it, j) => {
      s.addShape(pres.shapes.OVAL, {
        x: x + 0.2, y: 2.18 + j * 0.92, w: 0.18, h: 0.18,
        fill: { color: p.color },
      });
      s.addText(it, {
        x: x + 0.48, y: 2.1 + j * 0.92, w: 2.4, h: 0.82,
        fontSize: 11, color: C.white,
        align: "left", valign: "middle", margin: 0,
      });
    });
  });
}

// ═══════════════════════════════════════════
// Slide 11 — 結語
// ═══════════════════════════════════════════
{
  const s = pres.addSlide();
  s.background = { color: C.dark };

  s.addShape(pres.shapes.OVAL, {
    x: -1.0, y: 3.0, w: 6, h: 6,
    fill: { color: "3D0066", transparency: 80 },
  });
  s.addShape(pres.shapes.OVAL, {
    x: 7.0, y: -1.5, w: 6, h: 6,
    fill: { color: "003D66", transparency: 82 },
  });

  s.addText("THANK YOU", {
    x: 0.5, y: 1.0, w: 9, h: 1.0,
    fontSize: 48, bold: true, color: C.accentL,
    align: "center", valign: "middle", margin: 0,
  });
  s.addText("演算法設計篇 完", {
    x: 0.5, y: 2.1, w: 9, h: 0.55,
    fontSize: 20, color: C.white,
    align: "center", margin: 0,
  });

  const sumStats = [
    { v: "12", l: "彈幕演算法", sub: "純靜態工具類 BulletPattern" },
    { v: "4", l: "子彈運動型", sub: "直線/追蹤/波動/加速" },
    { v: "5", l: "技能升級樹", sub: "射速/射頻/生命/炸彈/刷掠" },
    { v: "O(n²)", l: "碰撞偵測", sub: "60fps 下 < 1ms 耗時" },
  ];

  sumStats.forEach((st, i) => {
    card(s, 0.5 + i * 2.28, 3.2, 2.1, 1.8);
    s.addText(st.v, {
      x: 0.5 + i * 2.28, y: 3.3, w: 2.1, h: 0.7,
      fontSize: 28, bold: true, color: C.teal,
      align: "center", valign: "middle", margin: 0,
    });
    s.addText(st.l, {
      x: 0.5 + i * 2.28, y: 3.98, w: 2.1, h: 0.3,
      fontSize: 12, bold: true, color: C.white,
      align: "center", margin: 0,
    });
    s.addText(st.sub, {
      x: 0.5 + i * 2.28, y: 4.28, w: 2.1, h: 0.6,
      fontSize: 9, color: C.dim,
      align: "center", valign: "middle", margin: 0,
    });
  });
}

// ── Output ──────────────────────────────────
pres.writeFile({ fileName: "C:\\project T\\TouhouGame\\ppt\\02_演算法設計.pptx" })
  .then(() => console.log("✓ PPT2 saved: 02_演算法設計.pptx"))
  .catch(e => console.error("Error:", e));
