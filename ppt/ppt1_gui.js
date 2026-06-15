// PPT 1 — 圖形介面設計篇
const pptxgen = require("pptxgenjs");

const pres = new pptxgen();
pres.layout = "LAYOUT_16x9";
pres.title = "Bullet Storm — 圖形介面設計";
pres.author = "BulletStorm Team";

// ── Palette ──────────────────────────────────────────
const C = {
  dark:   "0D1B2A",  // slide background (dark navy)
  panel:  "1B2A3B",  // content card
  accent: "00B4D8",  // cyan
  accentD:"0077A8",  // darker cyan
  gold:   "F4A261",  // warm accent
  white:  "F0F4F8",
  dim:    "8AA3B8",
  red:    "E63946",
  green:  "57CC99",
  purple: "9B5DE5",
};

// ── Helpers ───────────────────────────────────────────
function makeShadow() {
  return { type: "outer", color: "000000", blur: 8, offset: 3, angle: 45, opacity: 0.25 };
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
    x: 0.4, y: 0.18, w: 2.0, h: 0.36,
    fill: { color: C.accent },
    rectRadius: 0.06,
  });
  slide.addText(label, {
    x: 0.4, y: 0.18, w: 2.0, h: 0.36,
    fontSize: 11, bold: true, color: C.dark,
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

  // Large background circle (decorative)
  s.addShape(pres.shapes.OVAL, {
    x: 6.0, y: -1.5, w: 7, h: 7,
    fill: { color: C.accentD, transparency: 82 },
  });
  s.addShape(pres.shapes.OVAL, {
    x: 7.0, y: -0.5, w: 4, h: 4,
    fill: { color: C.accent, transparency: 88 },
  });

  // Game title badge
  s.addShape(pres.shapes.ROUNDED_RECTANGLE, {
    x: 0.5, y: 0.5, w: 2.8, h: 0.5,
    fill: { color: C.accent },
    rectRadius: 0.08,
  });
  s.addText("BULLET STORM", {
    x: 0.5, y: 0.5, w: 2.8, h: 0.5,
    fontSize: 13, bold: true, color: C.dark,
    align: "center", valign: "middle", margin: 0,
  });

  // Main headline
  s.addText("圖形介面設計", {
    x: 0.5, y: 1.25, w: 7, h: 1.2,
    fontSize: 48, bold: true, color: C.white,
    align: "left", valign: "middle", margin: 0,
  });
  s.addText("Graphical User Interface Design", {
    x: 0.5, y: 2.5, w: 7, h: 0.5,
    fontSize: 18, color: C.accent,
    align: "left", margin: 0,
  });

  // Divider
  s.addShape(pres.shapes.RECTANGLE, {
    x: 0.5, y: 3.1, w: 3.5, h: 0.04,
    fill: { color: C.gold },
  });

  // Subtitle info
  s.addText([
    { text: "彈幕遊戲  ·  Java Swing  ·  純圖形渲染", options: { breakLine: true } },
    { text: "A Danmaku Bullet-Hell Shooter", options: {} },
  ], {
    x: 0.5, y: 3.28, w: 7, h: 0.8,
    fontSize: 14, color: C.dim,
    align: "left", margin: 0,
  });

  // Stats cards
  const stats = [
    { v: "3", l: "關卡" },
    { v: "12", l: "彈幕模式" },
    { v: "60", l: "FPS" },
    { v: "~60", l: "FPS" },
  ];
  const statsReal = [
    { v: "3", l: "遊戲關卡" },
    { v: "12", l: "彈幕演算法" },
    { v: "60+", l: "穩定幀率" },
    { v: "10\"", l: "視窗寬度" },
  ];
  statsReal.forEach((st, i) => {
    card(s, 0.5 + i * 2.3, 4.4, 2.1, 0.9);
    s.addText(st.v, {
      x: 0.5 + i * 2.3, y: 4.42, w: 2.1, h: 0.45,
      fontSize: 24, bold: true, color: C.accent,
      align: "center", valign: "middle", margin: 0,
    });
    s.addText(st.l, {
      x: 0.5 + i * 2.3, y: 4.85, w: 2.1, h: 0.3,
      fontSize: 10, color: C.dim,
      align: "center", valign: "middle", margin: 0,
    });
  });

  s.addNotes("開場介紹：本簡報聚焦於 Bullet Storm 彈幕遊戲的圖形介面設計，涵蓋視窗佈局、HUD面板、角色繪製與視覺效果。");
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
    { n: "01", t: "專案動機", d: "為何製作彈幕遊戲？靈感來源與目標" },
    { n: "02", t: "系統架構", d: "視窗佈局、畫面分區、渲染管線" },
    { n: "03", t: "使用技術", d: "Java Swing / Graphics2D / 雙緩衝渲染" },
    { n: "04", t: "實驗結果", d: "HUD展示、特效、角色繪製成果" },
    { n: "05", t: "未來改進", d: "精靈圖、動畫系統、UI美化方向" },
  ];

  items.forEach((it, i) => {
    const y = 1.5 + i * 0.78;
    card(s, 0.4, y, 9.2, 0.65);
    // Number
    s.addShape(pres.shapes.OVAL, {
      x: 0.55, y: y + 0.1, w: 0.45, h: 0.45,
      fill: { color: C.accent },
    });
    s.addText(it.n, {
      x: 0.55, y: y + 0.1, w: 0.45, h: 0.45,
      fontSize: 12, bold: true, color: C.dark,
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
  slideTitle(s, "為什麼製作彈幕遊戲？");
  hRule(s, 1.35);

  // Left col — motivations
  const motives = [
    { icon: "🎮", title: "遊戲類型挑戰性高", desc: "彈幕遊戲需要精密的碰撞偵測與即時渲染，是絕佳的技術訓練場景" },
    { icon: "🖥️", title: "純 Java 圖形實作", desc: "不依賴外部遊戲引擎，完全使用 Java Swing / Graphics2D 實現所有視覺效果" },
    { icon: "⚙️", title: "演算法整合展示", desc: "12 種彈幕模式演算法結合 RPG 升級系統，展示程式設計的廣度與深度" },
    { icon: "🎓", title: "學術課程要求", desc: "專案涵蓋演算法設計、物件導向設計、GUI程式設計等多項學習目標" },
  ];

  motives.forEach((m, i) => {
    const y = 1.55 + i * 0.92;
    card(s, 0.4, y, 5.5, 0.78);
    s.addText(m.icon, {
      x: 0.55, y: y + 0.12, w: 0.55, h: 0.55,
      fontSize: 22, align: "center", valign: "middle", margin: 0,
    });
    s.addText(m.title, {
      x: 1.2, y: y + 0.08, w: 4.5, h: 0.28,
      fontSize: 13, bold: true, color: C.white,
      align: "left", margin: 0,
    });
    s.addText(m.desc, {
      x: 1.2, y: y + 0.36, w: 4.5, h: 0.35,
      fontSize: 10, color: C.dim,
      align: "left", margin: 0,
    });
  });

  // Right col — key goals
  card(s, 6.1, 1.55, 3.5, 3.68, "162032");
  s.addText("核心目標", {
    x: 6.2, y: 1.65, w: 3.3, h: 0.35,
    fontSize: 14, bold: true, color: C.accent,
    align: "center", margin: 0,
  });
  const goals = [
    "流暢 60 FPS 遊戲體驗",
    "直觀的 HUD 資訊顯示",
    "豐富的視覺粒子特效",
    "角色差異化外觀設計",
    "清晰的難度進度回饋",
    "完整的 EXP 升級視覺化",
  ];
  goals.forEach((g, i) => {
    s.addShape(pres.shapes.OVAL, {
      x: 6.25, y: 2.13 + i * 0.5, w: 0.18, h: 0.18,
      fill: { color: C.gold },
    });
    s.addText(g, {
      x: 6.55, y: 2.08 + i * 0.5, w: 2.8, h: 0.28,
      fontSize: 11, color: C.white,
      align: "left", valign: "middle", margin: 0,
    });
  });
}

// ═══════════════════════════════════════════
// Slide 4 — 系統架構：畫面佈局
// ═══════════════════════════════════════════
{
  const s = pres.addSlide();
  s.background = { color: C.dark };

  sectionTag(s, "02 系統架構");
  slideTitle(s, "視窗佈局設計");
  hRule(s, 1.35);

  // Window mockup
  card(s, 0.4, 1.5, 9.2, 3.8);

  // Field area
  s.addShape(pres.shapes.RECTANGLE, {
    x: 0.7, y: 1.7, w: 5.8, h: 3.35,
    fill: { color: "080820" },
    shadow: makeShadow(),
  });
  s.addText("遊戲場地\n FIELD  480×560 px", {
    x: 0.7, y: 2.8, w: 5.8, h: 1.0,
    fontSize: 13, color: C.dim, align: "center", valign: "middle", margin: 0,
  });

  // Grid lines (decorative)
  for (let i = 1; i < 5; i++) {
    s.addShape(pres.shapes.LINE, {
      x: 0.7, y: 1.7 + i * 0.67, w: 5.8, h: 0,
      line: { color: "1E2A40", width: 1 },
    });
  }
  for (let i = 1; i < 5; i++) {
    s.addShape(pres.shapes.LINE, {
      x: 0.7 + i * 1.16, y: 1.7, w: 0, h: 3.35,
      line: { color: "1E2A40", width: 1 },
    });
  }

  // HUD area
  s.addShape(pres.shapes.RECTANGLE, {
    x: 6.7, y: 1.7, w: 2.7, h: 3.35,
    fill: { color: "0A0A23" },
    shadow: makeShadow(),
  });

  // HUD content (mini)
  const hudItems = [
    { label: "HIGH SCORE", val: "9999999", y: 1.82 },
    { label: "SCORE", val: "00142680", y: 2.18 },
    { label: "HEALTH ♥♥♥", val: "", y: 2.55 },
    { label: "BOMBS ★★★", val: "", y: 2.88 },
    { label: "POWER", val: "■■■■□", y: 3.2 },
    { label: "GRAZE", val: "  142", y: 3.52 },
    { label: "LEVEL  7", val: "", y: 3.85 },
    { label: "▓▓▓▓▓▓▒▒", val: "", y: 4.05 },
  ];
  hudItems.forEach(it => {
    s.addText(it.label, {
      x: 6.75, y: it.y, w: 1.5, h: 0.24,
      fontSize: 7, color: C.accent, align: "left", margin: 0,
    });
    if (it.val) {
      s.addText(it.val, {
        x: 8.1, y: it.y, w: 1.2, h: 0.24,
        fontSize: 7, color: C.white, align: "right", margin: 0,
      });
    }
  });

  // Annotations
  const annots = [
    { x: 0.4, y: 5.1, text: "● 遊戲場地  (480 × 560)：敵人、子彈、玩家的互動空間，含格線裝飾" },
    { x: 4.8, y: 5.1, text: "● HUD 面板  (200 × 560)：右側固定顯示分數、生命、炸彈、EXP" },
  ];
  annots.forEach(a => {
    s.addText(a.text, {
      x: a.x, y: a.y, w: 4.3, h: 0.3,
      fontSize: 10, color: C.dim, align: "left", margin: 0,
    });
  });

  // Dimension labels
  s.addText("800 px", {
    x: 3.5, y: 1.52, w: 2, h: 0.2,
    fontSize: 9, color: C.gold, align: "center", margin: 0,
  });
  s.addText("600 px", {
    x: 0.07, y: 3.0, w: 0.6, h: 0.2,
    fontSize: 9, color: C.gold, align: "center", margin: 0,
  });
}

// ═══════════════════════════════════════════
// Slide 5 — 系統架構：渲染管線
// ═══════════════════════════════════════════
{
  const s = pres.addSlide();
  s.background = { color: C.dark };

  sectionTag(s, "02 系統架構");
  slideTitle(s, "渲染管線與遊戲迴圈");
  hRule(s, 1.35);

  // Pipeline steps
  const steps = [
    { n: "1", t: "遊戲邏輯更新", d: "玩家/敵人/子彈狀態更新\n碰撞偵測、EXP結算", color: C.accentD },
    { n: "2", t: "離屏緩衝繪製", d: "繪製至 BufferedImage\n避免畫面撕裂 (double buffer)", color: C.purple },
    { n: "3", t: "場景分層渲染", d: "星空背景 → 粒子 → 子彈\n→ 敵人 → 玩家 → UI", color: C.gold },
    { n: "4", t: "輸出至螢幕", d: "將緩衝圖像貼至 JPanel\n螢幕震動偏移計算", color: C.green },
  ];

  steps.forEach((st, i) => {
    const x = 0.4 + i * 2.35;
    card(s, x, 1.55, 2.15, 2.8);
    s.addShape(pres.shapes.OVAL, {
      x: x + 0.83, y: 1.65, w: 0.5, h: 0.5,
      fill: { color: st.color },
    });
    s.addText(st.n, {
      x: x + 0.83, y: 1.65, w: 0.5, h: 0.5,
      fontSize: 16, bold: true, color: C.dark,
      align: "center", valign: "middle", margin: 0,
    });
    s.addText(st.t, {
      x: x + 0.1, y: 2.25, w: 1.95, h: 0.45,
      fontSize: 13, bold: true, color: C.white,
      align: "center", valign: "middle", margin: 0,
    });
    s.addText(st.d, {
      x: x + 0.1, y: 2.78, w: 1.95, h: 0.8,
      fontSize: 10, color: C.dim,
      align: "center", valign: "top", margin: 0,
    });
    // Arrow
    if (i < 3) {
      s.addShape(pres.shapes.LINE, {
        x: x + 2.15, y: 2.95, w: 0.2, h: 0,
        line: { color: C.accent, width: 2 },
      });
    }
  });

  // Timer info
  card(s, 0.4, 4.55, 9.2, 0.85);
  s.addText("⏱  javax.swing.Timer  @  16ms 間隔  ≈  62.5 FPS  |  paintComponent() 繪製目標每幀更新", {
    x: 0.5, y: 4.6, w: 9.0, h: 0.75,
    fontSize: 13, color: C.accent, bold: true,
    align: "center", valign: "middle", margin: 0,
  });
}

// ═══════════════════════════════════════════
// Slide 6 — 使用技術
// ═══════════════════════════════════════════
{
  const s = pres.addSlide();
  s.background = { color: C.dark };

  sectionTag(s, "03 使用技術");
  slideTitle(s, "圖形渲染技術棧");
  hRule(s, 1.35);

  const techs = [
    {
      cat: "核心框架", color: C.accent,
      items: ["Java Swing — JPanel + JFrame 視窗管理", "javax.swing.Timer — 遊戲主迴圈計時器", "ActionListener — 每幀回呼介面"],
    },
    {
      cat: "繪圖引擎", color: C.gold,
      items: ["Graphics2D — 2D 向量繪圖引擎", "RenderingHints — 抗鋸齒 (ANTIALIAS)", "GradientPaint — 漸層色彩效果", "BasicStroke — 自訂線條樣式"],
    },
    {
      cat: "效能最佳化", color: C.green,
      items: ["BufferedImage — 離屏雙緩衝", "Shape.setClip() — 場地裁切渲染", "ArrayList 快速迭代子彈清單", "removeIf() 批次清除無效物件"],
    },
    {
      cat: "視覺效果", color: C.purple,
      items: ["Particle System — 爆炸粒子系統", "Screen Shake — 畫面震動偏移", "Alpha 透明度 — 半透明光暈效果", "Polygon / Ellipse — 角色手繪外型"],
    },
  ];

  techs.forEach((t, i) => {
    const col = i % 2, row = Math.floor(i / 2);
    const x = 0.4 + col * 4.7, y = 1.55 + row * 2.0;
    card(s, x, y, 4.5, 1.8);
    s.addShape(pres.shapes.ROUNDED_RECTANGLE, {
      x: x + 0.12, y: y + 0.1, w: 1.6, h: 0.3,
      fill: { color: t.color },
      rectRadius: 0.05,
    });
    s.addText(t.cat, {
      x: x + 0.12, y: y + 0.1, w: 1.6, h: 0.3,
      fontSize: 10, bold: true, color: C.dark,
      align: "center", valign: "middle", margin: 0,
    });
    t.items.forEach((item, j) => {
      s.addShape(pres.shapes.OVAL, {
        x: x + 0.18, y: y + 0.52 + j * 0.32, w: 0.1, h: 0.1,
        fill: { color: t.color },
      });
      s.addText(item, {
        x: x + 0.36, y: y + 0.46 + j * 0.32, w: 4.0, h: 0.28,
        fontSize: 11, color: C.white,
        align: "left", valign: "middle", margin: 0,
      });
    });
  });
}

// ═══════════════════════════════════════════
// Slide 7 — 實驗結果：HUD 設計成果
// ═══════════════════════════════════════════
{
  const s = pres.addSlide();
  s.background = { color: C.dark };

  sectionTag(s, "04 實驗結果");
  slideTitle(s, "HUD 介面設計成果");
  hRule(s, 1.35);

  // Left: HUD feature list
  const features = [
    { icon: "📊", title: "分數系統", desc: "HIGH SCORE + 即時 SCORE，等寬字型確保對齊" },
    { icon: "❤️", title: "生命顯示", desc: "圖示化 ♥ 心形顯示，最多 8 格，直觀反映狀態" },
    { icon: "💣", title: "炸彈顯示", desc: "★ 星形圖示，升級後炸彈傷害加成視覺標示" },
    { icon: "⚡", title: "能量條", desc: "漸層色能量條，4 個力量段位視覺切割" },
    { icon: "🎯", title: "刷掠計數", desc: "子彈掠過時累計 Graze 點數，刺激風險操作" },
    { icon: "🌟", title: "EXP 升級面板", desc: "等級、EXP 進度條、5 技能星等即時顯示" },
  ];

  features.forEach((f, i) => {
    const col = i % 2, row = Math.floor(i / 2);
    const x = 0.4 + col * 4.7, y = 1.55 + row * 1.28;
    card(s, x, y, 4.5, 1.1);
    s.addText(f.icon, {
      x: x + 0.12, y: y + 0.28, w: 0.55, h: 0.55,
      fontSize: 20, align: "center", valign: "middle", margin: 0,
    });
    s.addText(f.title, {
      x: x + 0.75, y: y + 0.1, w: 3.6, h: 0.35,
      fontSize: 13, bold: true, color: C.accent,
      align: "left", valign: "middle", margin: 0,
    });
    s.addText(f.desc, {
      x: x + 0.75, y: y + 0.45, w: 3.6, h: 0.55,
      fontSize: 10, color: C.dim,
      align: "left", valign: "top", margin: 0,
    });
  });
}

// ═══════════════════════════════════════════
// Slide 8 — 實驗結果：視覺效果
// ═══════════════════════════════════════════
{
  const s = pres.addSlide();
  s.background = { color: C.dark };

  sectionTag(s, "04 實驗結果");
  slideTitle(s, "視覺效果成果展示");
  hRule(s, 1.35);

  // Effect demos using shapes
  const demos = [
    {
      title: "子彈光暈效果", x: 0.4, y: 1.55, w: 3.0, h: 2.2,
      desc: "三層同心圓：外層半透明光暈\n主色圓體 + 白色核心亮點",
    },
    {
      title: "粒子爆炸系統", x: 3.6, y: 1.55, w: 3.0, h: 2.2,
      desc: "敵人擊毀產生 20 顆粒子\n重力下落 + Alpha 淡出",
    },
    {
      title: "玩家角色繪製", x: 6.6, y: 1.55, w: 3.0, h: 2.2,
      desc: "手工多邊形繪製\n極限模式顯示旋轉菱形判定圈",
    },
  ];

  demos.forEach(d => {
    card(s, d.x, d.y, d.w, d.h);
    s.addText(d.title, {
      x: d.x + 0.1, y: d.y + 0.1, w: d.w - 0.2, h: 0.35,
      fontSize: 12, bold: true, color: C.accent,
      align: "center", margin: 0,
    });
    // Visual placeholder
    s.addShape(pres.shapes.OVAL, {
      x: d.x + d.w/2 - 0.7, y: d.y + 0.55, w: 1.4, h: 1.4,
      fill: { color: C.panel },
      shadow: makeShadow(),
    });
    s.addText("✦", {
      x: d.x + d.w/2 - 0.7, y: d.y + 0.55, w: 1.4, h: 1.4,
      fontSize: 30, color: C.accent,
      align: "center", valign: "middle", margin: 0,
    });
    s.addText(d.desc, {
      x: d.x + 0.1, y: d.y + 1.75, w: d.w - 0.2, h: 0.4,
      fontSize: 9.5, color: C.dim,
      align: "center", valign: "middle", margin: 0,
    });
  });

  // Performance chart
  const perfData = [
    { label: "0 子彈", val: 62 },
    { label: "100 子彈", val: 61 },
    { label: "200 子彈", val: 60 },
    { label: "300 子彈", val: 59 },
    { label: "400 子彈", val: 57 },
  ];

  s.addText("幀率 vs. 場景物件數量", {
    x: 0.4, y: 3.85, w: 5, h: 0.3,
    fontSize: 13, bold: true, color: C.white,
    align: "left", margin: 0,
  });

  s.addChart(pres.charts.LINE, [{
    name: "FPS",
    labels: perfData.map(p => p.label),
    values: perfData.map(p => p.val),
  }], {
    x: 0.4, y: 4.2, w: 9.2, h: 1.2,
    chartColors: [C.accent],
    chartArea: { fill: { color: C.panel }, roundedCorners: true },
    catAxisLabelColor: C.dim,
    valAxisLabelColor: C.dim,
    valGridLine: { color: "1E2A40", size: 0.5 },
    catGridLine: { style: "none" },
    showLegend: false,
    lineSize: 3,
    lineSmooth: true,
  });
}

// ═══════════════════════════════════════════
// Slide 9 — 未來改進
// ═══════════════════════════════════════════
{
  const s = pres.addSlide();
  s.background = { color: C.dark };

  sectionTag(s, "05 未來改進");
  slideTitle(s, "圖形介面改進方向");
  hRule(s, 1.35);

  const improvements = [
    {
      phase: "短期", color: C.green,
      items: [
        "載入外部精靈圖 (Sprite Sheet) 取代程式繪製角色",
        "背景捲動圖層 (Parallax Scrolling)",
        "音效與 BGM 整合 (javax.sound)",
      ],
    },
    {
      phase: "中期", color: C.gold,
      items: [
        "GPU 加速渲染 (JOGL / JavaFX Canvas)",
        "自訂字體 TTF 載入，提升文字美感",
        "動畫狀態機：角色移動/受傷動畫幀",
      ],
    },
    {
      phase: "長期", color: C.red,
      items: [
        "切換至 JavaFX 或 LibGDX 框架",
        "著色器效果 (Shader)：光暈、閃光、扭曲",
        "支援 4K 解析度與多螢幕縮放",
      ],
    },
  ];

  improvements.forEach((imp, i) => {
    const x = 0.4 + i * 3.13;
    card(s, x, 1.55, 3.0, 3.7);
    s.addShape(pres.shapes.ROUNDED_RECTANGLE, {
      x: x + 0.7, y: 1.65, w: 1.6, h: 0.38,
      fill: { color: imp.color },
      rectRadius: 0.08,
    });
    s.addText(imp.phase + "目標", {
      x: x + 0.7, y: 1.65, w: 1.6, h: 0.38,
      fontSize: 12, bold: true, color: C.dark,
      align: "center", valign: "middle", margin: 0,
    });
    imp.items.forEach((it, j) => {
      s.addShape(pres.shapes.OVAL, {
        x: x + 0.2, y: 2.18 + j * 0.92, w: 0.18, h: 0.18,
        fill: { color: imp.color },
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
// Slide 10 — 結語
// ═══════════════════════════════════════════
{
  const s = pres.addSlide();
  s.background = { color: C.dark };

  s.addShape(pres.shapes.OVAL, {
    x: -1.5, y: 3.5, w: 6, h: 6,
    fill: { color: C.accentD, transparency: 85 },
  });
  s.addShape(pres.shapes.OVAL, {
    x: 7.5, y: -1.0, w: 5, h: 5,
    fill: { color: C.accent, transparency: 90 },
  });

  s.addText("THANK YOU", {
    x: 0.5, y: 1.0, w: 9, h: 1.0,
    fontSize: 48, bold: true, color: C.accent,
    align: "center", valign: "middle", margin: 0,
  });
  s.addText("圖形介面設計篇 完", {
    x: 0.5, y: 2.1, w: 9, h: 0.55,
    fontSize: 20, color: C.white,
    align: "center", margin: 0,
  });

  // Summary stats
  const sumStats = [
    { v: "3", l: "畫面狀態機", sub: "START / PLAYING / GAME OVER" },
    { v: "6", l: "HUD 模組", sub: "分數/生命/炸彈/能量/EXP/Boss" },
    { v: "∞", l: "粒子特效", sub: "爆炸 + 炸彈 + 光暈" },
    { v: "60", l: "FPS 目標", sub: "javax.swing.Timer @16ms" },
  ];

  sumStats.forEach((st, i) => {
    card(s, 0.5 + i * 2.28, 3.2, 2.1, 1.8);
    s.addText(st.v, {
      x: 0.5 + i * 2.28, y: 3.3, w: 2.1, h: 0.7,
      fontSize: 32, bold: true, color: C.accent,
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
pres.writeFile({ fileName: "C:\\project T\\TouhouGame\\ppt\\01_圖形介面設計.pptx" })
  .then(() => console.log("✓ PPT1 saved: 01_圖形介面設計.pptx"))
  .catch(e => console.error("Error:", e));
