package org.awesome.fabricclient.client.gui;

import net.minecraft.client.Minecraft;
import org.awesome.fabricclient.client.module.Category;
import org.joml.Matrix3x2fStack;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import org.awesome.fabricclient.client.module.Module;
import org.awesome.fabricclient.client.module.ModuleManager;
import org.awesome.fabricclient.client.module.settings.*;

import java.util.List;

public class ClickGui extends Screen {

    private static final int GUI_W = 640;
    private static final int GUI_H = 360;
    private static final int GUI_SETTINGS_W = 192;

    private static final int SIDEBAR_W = 76;
    private static final int SIDEBAR_PAD = 8;
    private static final int CAT_H = 24;

    private static final int CONTENT_PAD = 10;
    private static final int CARD_COLS = 2;
    private static final int CARD_GAP = 6;
    private static final int CARD_H = 40;

    private static final int PILL_W = 30;
    private static final int PILL_H = 14;

    private static final int UI_SCALE = 2;
    private static final int FIT_PAD = 12;

    private double guiScale = 1.0;
    private int uiScale = UI_SCALE;

    private int fbW() {
        return (int)(this.width * guiScale);
    }

    private int fbH() {
        return (int)(this.height * guiScale);
    }

    private int viewW() {
        return (int)(fbW() / uiScale);
    }

    private int viewH() {
        return (int)(fbH() / uiScale);
    }

    private static final int SCROLL_SPEED = 10;

    private static final int C_BG = 0xF20D1117;
    private static final int C_BG_DARK = 0xF0010409;
    private static final int C_TAB_BAR = 0xF0161B22;
    private static final int C_TAB_ACTIVE = 0xF01F242C;
    private static final int C_TAB_HOVER = 0xBB1C2128;
    private static final int C_TAB_TEXT = 0xFFFFFFFF;
    private static final int C_TAB_TEXT_DIM = 0xFF8B949E;
    private static final int C_BORDER = 0xFF30363D;
    private static final int C_CARD = 0xCC161B22;
    private static final int C_CARD_HOVER = 0xCC1F242C;
    private static final int C_CARD_BORDER = 0xFF30363D;
    private static final int C_CARD_SEL = 0xCC21262F;
    private static final int C_CARD_SEL_BRD = 0xFFE8701A;
    private static final int C_TEXT = 0xFFF0F6FC;
    private static final int C_TEXT_DIM = 0xFFC9D1D9;
    private static final int C_TEXT_DESC = 0xFF8B949E;
    private static final int C_ACCENT = 0xFFE8701A;
    private static final int C_SET_BG = 0xF00D1117;
    private static final int C_SET_BORDER = 0xFF30363D;
    private static final int C_SLIDER_BG = 0xFF21262F;
    private static final int C_SLIDER_FILL = 0xFFE8701A;
    private static final int C_SLIDER_KNOB = 0xFFFFFFFF;
    private static final int C_OVERLAY = 0xAA010409;
    private static final int C_SCROLLBAR = 0xFF161B22;
    private static final int C_SCROLLBAR_TH = 0xFF30363D;

    private Category activeTab = Category.COMBAT;
    private Module settingsModule = null;

    private int moduleScroll = 0;
    private int settingsScroll = 0;

    private boolean draggingSlider = false;
    private SliderSetting activeSlider = null;
    private RangeSliderSetting activeRangeSlider = null;
    private boolean draggingRangeMax = false;
    private int sliderBarX, sliderBarW;
    private InputSetting activeInput = null;

    public ClickGui() {
        super(Component.literal("ClickGUI"));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
        super.extractRenderState(g, mx, my, delta);

        guiScale = Minecraft.getInstance().getWindow().getGuiScale();

        int needW = (settingsModule != null ? GUI_W + GUI_SETTINGS_W : GUI_W) + FIT_PAD * 2;
        int needH = GUI_H + FIT_PAD * 2;
        int kMax = Math.max(1, Math.min(fbW() / needW, fbH() / needH));
        uiScale = Math.min(UI_SCALE, kMax);

        Matrix3x2fStack ps = g.pose();
        ps.pushMatrix();
        ps.scale((float)(uiScale / guiScale), (float)(uiScale / guiScale));

        int rmx = (int)(mx * guiScale / uiScale);
        int rmy = (int)(my * guiScale / uiScale);

        g.fill(0, 0, viewW(), viewH(), C_OVERLAY);

        int gx = guiX(), gy = guiY();
        int gw = GUI_W, gh = GUI_H;

        g.fill(gx + 4, gy + 4, gx + gw + 4, gy + gh + 4, 0x55000000);

        g.fill(gx, gy, gx + gw, gy + gh, C_BG);
        g.outline(gx, gy, gw, gh, C_BORDER);

        g.fill(gx, gy, gx + SIDEBAR_W, gy + gh, C_TAB_BAR);
        g.fill(gx + SIDEBAR_W - 1, gy, gx + SIDEBAR_W, gy + gh, C_BORDER);

        renderSidebar(g, gx, gy, gh, rmx, rmy);

        int contentX = gx + SIDEBAR_W;
        int contentW = gw - SIDEBAR_W;
        renderContent(g, contentX, gy, contentW, gh, rmx, rmy);

        if (settingsModule != null) {
            renderSettingsPanel(g, gx + gw, gy, gh, rmx, rmy);
        }

        ps.popMatrix();
    }

    private void renderSidebar(GuiGraphicsExtractor g, int gx, int gy, int gh, int mx, int my) {
        g.fill(gx, gy, gx + SIDEBAR_W, gy + 28, C_BG_DARK);
        g.fill(gx, gy + 28, gx + SIDEBAR_W, gy + 29, C_ACCENT);
        g.text(this.font, nice("Awesome"), gx + SIDEBAR_PAD, gy + 9, C_ACCENT, true);

        int catY = gy + 36;
        for (Category cat : Category.values()) {
            boolean active = cat == activeTab;
            boolean hovered = !active && isIn(mx, my, gx, catY, SIDEBAR_W, CAT_H);

            if (active) {
                g.fill(gx, catY, gx + SIDEBAR_W, catY + CAT_H, C_TAB_ACTIVE);
                g.fill(gx, catY, gx + 2, catY + CAT_H, C_ACCENT);
            } else if (hovered) {
                g.fill(gx, catY, gx + SIDEBAR_W, catY + CAT_H, C_TAB_HOVER);
            }

            int textCol = active ? C_TAB_TEXT : (hovered ? C_TAB_TEXT : C_TAB_TEXT_DIM);
            g.text(this.font, nice(cat.displayName), gx + SIDEBAR_PAD + 6, catY + (CAT_H - 8) / 2, textCol, active);

            catY += CAT_H;
        }
    }

    private void renderContent(GuiGraphicsExtractor g, int cx, int cy, int cw, int ch, int mx, int my) {
        List<Module> mods = ModuleManager.getInstance().getModulesByCategory(activeTab);
        g.fill(cx, cy, cx + cw, cy + ch, C_BG_DARK);

        int cardW = (cw - CONTENT_PAD * 2 - CARD_GAP * (CARD_COLS - 1)) / CARD_COLS;
        int rows = (mods.size() + CARD_COLS - 1) / CARD_COLS;
        int totalH = rows * (CARD_H + CARD_GAP) - CARD_GAP + CONTENT_PAD * 2;

        int maxScroll = Math.max(0, totalH - ch);
        moduleScroll = Math.max(0, Math.min(moduleScroll, maxScroll));

        for (int i = 0; i < mods.size(); i++) {
            Module mod = mods.get(i);
            int cardX = cx + CONTENT_PAD + (i % CARD_COLS) * (cardW + CARD_GAP);
            int cardY = cy + CONTENT_PAD + (i / CARD_COLS) * (CARD_H + CARD_GAP) - moduleScroll;
            if (cardY + CARD_H < cy || cardY > cy + ch) continue;
            boolean hov = isIn(mx, my, cardX, cardY, cardW, CARD_H);
            drawCard(g, mod, cardX, cardY, cardW, hov);
        }

        if (mods.isEmpty()) {
            String msg = "No modules in this category.";
            g.text(this.font, nice(msg), cx + cw / 2 - niceW(msg) / 2, cy + ch / 2 - 4, C_TEXT_DIM, false);
        }

        if (maxScroll > 0) drawScrollbar(g, cx + cw - 5, cy + 2, 4, ch - 4, moduleScroll, totalH);
    }

    private void drawCard(GuiGraphicsExtractor g, Module mod, int x, int y, int w, boolean hov) {
        boolean selected = mod == settingsModule;
        int bg = selected ? C_CARD_SEL : (hov ? C_CARD_HOVER : C_CARD);
        int brd = selected ? C_CARD_SEL_BRD : C_CARD_BORDER;

        g.fill(x, y, x + w, y + CARD_H, bg);
        g.outline(x, y, w, CARD_H, brd);

        if (mod.isEnabled()) g.fill(x, y + 3, x + 2, y + CARD_H - 3, C_ACCENT);

        int nameColor = mod.isEnabled() ? C_TEXT : C_TEXT_DIM;
        g.text(this.font, nice(mod.getName()), x + 10, y + 9, nameColor, mod.isEnabled());

        String bind = "NONE";
        int tagX = x + 10 + niceW(mod.getName()) + 6;
        g.fill(tagX - 2, y + 7, tagX + niceW(bind) + 3, y + 19, 0x66161B22);
        g.text(this.font, nice(bind), tagX, y + 9, 0xFF8B949E, false);

        if (hasVisibleSettings(mod)) {
            String plus = "+";
            int plusX = tagX + niceW(bind) + 7;
            int plusW = niceW(plus);
            g.fill(plusX - 3, y + 7, plusX + plusW + 3, y + 19, 0x33E8701A);
            g.text(this.font, nice(plus), plusX, y + 9, C_ACCENT, true);
        }

        String desc = mod.getDescription();
        if (desc != null && !desc.isEmpty()) {
            g.text(this.font, nice(trimW(desc, w - PILL_W - 22)), x + 10, y + 22, C_TEXT_DESC, false);
        }

        drawPill(g, mod.isEnabled(), x + w - PILL_W - 8, y + CARD_H / 2 - PILL_H / 2);
    }

    private void drawPill(GuiGraphicsExtractor g, boolean on, int x, int y) {
        int up = Math.max(1, uiScale);
        int tw = PILL_W * up;
        int th = PILL_H * up;
        double tr = (th - 2.0) / 2.0;

        Matrix3x2fStack ps = g.pose();
        ps.pushMatrix();
        ps.translate((float) x, (float) y);
        ps.scale(1.0f / up, 1.0f / up);

        if (on) {
            drawAARoundRect(g, -3 * up, -3 * up, tw + 6 * up, th + 6 * up, tr + 3 * up, 0x10E8701A);
            drawAARoundRect(g, -2 * up, -2 * up, tw + 4 * up, th + 4 * up, tr + 2 * up, 0x1FE8701A);
            drawAARoundRect(g, -up, -up, tw + 2 * up, th + 2 * up, tr + up, 0x3AE8701A);
        }

        drawAARoundRect(g, 0, 2 * up, tw, th, tr, 0x33000000);
        drawAARoundRect(g, 0, up, tw, th, tr, 0x55000000);

        int trackTop = on ? 0xFFFF8526 : 0xFF3A4049;
        int trackBot = on ? 0xFFD15F10 : 0xFF22272E;
        drawVerticalGradientPill(g, 0, 0, tw, th, tr, trackTop, trackBot);

        drawAARoundRect(g, up, up, tw - 2 * up, Math.max(1, up), tr - 1, on ? 0x55FFFFFF : 0x33FFFFFF);
        drawAARoundRect(g, up, th - up - 1, tw - 2 * up, Math.max(1, up), tr - 1, 0x33000000);

        int kSize = th - 4 * up;
        double kr = kSize / 2.0;
        int kx = on ? tw - kSize - 2 * up : 2 * up;
        int ky = 2 * up;

        drawAARoundRect(g, kx - 1, ky + 2 * up, kSize + 2, kSize, kr + 1, 0x14000000);
        drawAARoundRect(g, kx, ky + 2 * up, kSize, kSize, kr, 0x33000000);
        drawAARoundRect(g, kx, ky + up, kSize, kSize, kr, 0x66000000);

        drawVerticalGradientPill(g, kx, ky, kSize, kSize, kr, 0xFFFFFFFF, 0xFFE0E6EE);

        drawAARoundRect(g, kx + up, ky, kSize - 2 * up, Math.max(1, up), kr - up, 0x77FFFFFF);
        drawAARoundRect(g, kx + up, ky + kSize - up - 1, kSize - 2 * up, Math.max(1, up), kr - up, 0x22000000);

        ps.popMatrix();
    }

    private void drawVerticalGradientPill(GuiGraphicsExtractor g, int x, int y, int w, int h, double r, int top, int bot) {
        if (w <= 0 || h <= 0) return;
        int aT = (top >>> 24) & 0xFF, rT = (top >>> 16) & 0xFF, gT = (top >>> 8) & 0xFF, bT = top & 0xFF;
        int aB = (bot >>> 24) & 0xFF, rB = (bot >>> 16) & 0xFF, gB = (bot >>> 8) & 0xFF, bB = bot & 0xFF;
        for (int j = 0; j < h; j++) {
            double t = (h <= 1) ? 0 : (double) j / (h - 1);
            int a = (int)(aT + (aB - aT) * t + 0.5);
            int rC = (int)(rT + (rB - rT) * t + 0.5);
            int gC = (int)(gT + (gB - gT) * t + 0.5);
            int bC = (int)(bT + (bB - bT) * t + 0.5);
            int c = (a << 24) | (rC << 16) | (gC << 8) | bC;
            drawAARoundRectRow(g, x, y, w, h, r, j, c);
        }
    }

    private void drawAARoundRectRow(GuiGraphicsExtractor g, int x, int y, int w, int h, double r, int j, int color) {
        int alphaBase = (color >>> 24) & 0xFF;
        if (alphaBase == 0) return;
        int rgb = color & 0x00FFFFFF;
        r = Math.max(0, Math.min(r, Math.min(w, h) / 2.0));
        double yc = j + 0.5;
        double dy;
        if (yc < r) dy = r - yc;
        else if (yc > h - r) dy = yc - (h - r);
        else {
            g.fill(x, y + j, x + w, y + j + 1, color);
            return;
        }
        if (dy >= r) return;
        double inset = r - Math.sqrt(r * r - dy * dy);
        if (inset * 2 >= w) return;
        int leftEdge = (int) Math.floor(inset);
        double frac = inset - leftEdge;
        double edgeCov = 1.0 - frac;
        int edgeAlpha = (int)(alphaBase * edgeCov + 0.5);
        int edgeC = (edgeAlpha << 24) | rgb;
        int innerL = leftEdge + 1;
        int innerR = w - leftEdge - 1;
        if (innerL < innerR) g.fill(x + innerL, y + j, x + innerR, y + j + 1, color);
        if (edgeAlpha > 0 && leftEdge < w / 2) {
            g.fill(x + leftEdge, y + j, x + leftEdge + 1, y + j + 1, edgeC);
            g.fill(x + w - leftEdge - 1, y + j, x + w - leftEdge, y + j + 1, edgeC);
        }
    }

    private void drawAARoundRect(GuiGraphicsExtractor g, int x, int y, int w, int h, double r, int color) {
        if (w <= 0 || h <= 0) return;
        int alphaBase = (color >>> 24) & 0xFF;
        if (alphaBase == 0) return;
        int rgb = color & 0x00FFFFFF;
        r = Math.max(0, Math.min(r, Math.min(w, h) / 2.0));
        if (r < 0.5) {
            g.fill(x, y, x + w, y + h, color);
            return;
        }
        int rInt = (int) Math.ceil(r);
        int midH = h - 2 * rInt;
        if (midH > 0) g.fill(x, y + rInt, x + w, y + h - rInt, color);
        int midW = w - 2 * rInt;
        if (midW > 0) {
            g.fill(x + rInt, y, x + w - rInt, y + rInt, color);
            g.fill(x + rInt, y + h - rInt, x + w - rInt, y + h, color);
        }
        for (int j = 0; j < rInt; j++) {
            int fullL = -1, fullR = -1;
            for (int i = 0; i < rInt; i++) {
                double dx = rInt - i - 0.5;
                double dy = rInt - j - 0.5;
                double d = Math.sqrt(dx * dx + dy * dy);
                double cov = r - d + 0.5;
                if (cov <= 0) continue;
                if (cov >= 1) {
                    if (fullL < 0) fullL = i;
                    fullR = i + 1;
                } else {
                    int alpha = (int)(alphaBase * cov + 0.5);
                    if (alpha <= 0) continue;
                    int c = (alpha << 24) | rgb;
                    g.fill(x + i, y + j, x + i + 1, y + j + 1, c);
                    g.fill(x + w - 1 - i, y + j, x + w - i, y + j + 1, c);
                    g.fill(x + i, y + h - 1 - j, x + i + 1, y + h - j, c);
                    g.fill(x + w - 1 - i, y + h - 1 - j, x + w - i, y + h - j, c);
                }
            }
            if (fullL >= 0) {
                g.fill(x + fullL, y + j, x + fullR, y + j + 1, color);
                g.fill(x + w - fullR, y + j, x + w - fullL, y + j + 1, color);
                g.fill(x + fullL, y + h - 1 - j, x + fullR, y + h - j, color);
                g.fill(x + w - fullR, y + h - 1 - j, x + w - fullL, y + h - j, color);
            }
        }
    }

    private void renderSettingsPanel(GuiGraphicsExtractor g, int px, int py, int gh, int mx, int my) {
        int sw = settingsW();

        List<Setting<?>> settings = settingsModule.getSettings();
        int totalSH = 8;
        int visibleCount = 0;
        for (Setting<?> s : settings) {
            if (!s.isVisible()) continue;
            totalSH += settingH(s) + 4;
            visibleCount++;
        }

        if(visibleCount == 0) {
            return;
        }

        g.fill(px + 3, py + 3, px + sw + 3, py + gh + 3, 0x44000000);
        g.fill(px, py, px + sw, py + gh, C_SET_BG);
        g.outline(px, py, sw, gh, C_SET_BORDER);

        g.fill(px, py, px + sw, py + 28, C_TAB_BAR);
        g.fill(px, py + 27, px + sw, py + 28, C_BORDER);
        g.fill(px, py + 26, px + sw, py + 28, C_ACCENT);
        g.text(this.font, nice(settingsModule.getName()), px + 8, py + 9, C_TEXT, true);

        int closeX = px + sw - 22, closeY = py + 6, closeS = 16;
        boolean closeHov = isIn(mx, my, closeX, closeY, closeS, closeS);
        if (closeHov) {
            drawCapsule(g, closeX, closeY, closeS, closeS, 0x22FFFFFF);
        }

        g.text(this.font, nice("×"), px + sw - 14, py + 9, closeHov ? C_TEXT : C_TEXT_DIM, closeHov);

        int contentY = py + 28;
        int contentH = gh - 28;

        int maxScroll = Math.max(0, totalSH - contentH);
        settingsScroll = Math.max(0, Math.min(settingsScroll, maxScroll));

        int sy = contentY + 8 - settingsScroll;
        for (Setting<?> s : settings) {
            if (!s.isVisible()) continue;
            int sh = settingH(s);
            if (sy + sh >= contentY && sy <= contentY + contentH) {
                boolean hov = isIn(mx, my, px + 6, sy, sw - 12, sh);
                drawSetting(g, s, px + 6, sy, sw - 12, hov);
            }
            sy += sh + 4;
        }

        if (visibleCount == 0) {
            return;
//            g.text(this.font, nice("No settings."), px + 8, contentY + 12, C_TEXT_DIM, false);
        }

        if (maxScroll > 0) drawScrollbar(g, px + sw - 5, contentY + 2, 4, contentH - 4, settingsScroll, totalSH);
    }

    private boolean hasVisibleSettings(Module mod) {
        for (Setting<?> s : mod.getSettings()) {
            if (s.isVisible()) return true;
        }
        return false;
    }

    private int settingH(Setting<?> s) {
        if (s instanceof SliderSetting) return 42;
        if (s instanceof RangeSliderSetting) return 42;
        return 30;
    }

    private void drawSetting(GuiGraphicsExtractor g, Setting<?> s, int x, int y, int w, boolean hov) {
        int sh = settingH(s);
        if (hov) g.fill(x - 4, y, x + w + 4, y + sh, 0x18FFFFFF);
        g.fill(x, y + sh, x + w, y + sh + 1, 0x22FFFFFF);

        int textY = y + (sh - 8) / 2;
        int sliderTextY = y + 8;

        if (s instanceof BooleanSetting b) {
            g.text(this.font, nice(b.getName()), x + 4, textY, C_TEXT_DIM, true);
            drawPill(g, b.getValue(), x + w - PILL_W - 4, y + sh / 2 - PILL_H / 2);

        } else if (s instanceof SliderSetting sl) {
            double pct = (double)(sl.getValue() - sl.getMin()) / (sl.getMax() - sl.getMin());
            int bx = x + 4, bw = w - 8, by = y + 28;
            int knob = bx + (int)(bw * pct);
            String val = String.format("%d", sl.getValue());
            g.text(this.font, nice(sl.getName()), x + 4, sliderTextY, C_TEXT_DIM, true);
            g.text(this.font, nice(val), x + w - niceW(val) - 4, sliderTextY, C_TEXT, true);
            drawCapsule(g, bx, by, bw, 4, C_SLIDER_BG);
            drawCapsule(g, bx, by, knob - bx, 4, C_SLIDER_FILL);
            drawCapsule(g, knob - 5, by - 2, 10, 10, 0x55000000);
            drawCapsule(g, knob - 5, by - 1, 10, 10, 0x1C000000);
            drawCapsule(g, knob - 5, by - 3, 10, 10, C_SLIDER_KNOB);
            drawCapsule(g, knob - 4, by - 3, 8, 1, 0x55FFFFFF);

        } else if (s instanceof RangeSliderSetting rs) {
            double range = rs.getMax() - rs.getMin();
            double pctMin = (rs.getMinValue() - rs.getMin()) / range;
            double pctMax = (rs.getMaxValue() - rs.getMin()) / range;
            int bx = x + 4, bw = w - 8, by = y + 28;
            int knobMin = bx + (int)(bw * pctMin);
            int knobMax = bx + (int)(bw * pctMax);
            String val = String.format("%.1f - %.1f", rs.getMinValue(), rs.getMaxValue());
            g.text(this.font, nice(rs.getName()), x + 4, sliderTextY, C_TEXT_DIM, true);
            g.text(this.font, nice(val), x + w - niceW(val) - 4, sliderTextY, C_TEXT, true);
            drawCapsule(g, bx, by, bw, 4, C_SLIDER_BG);
            drawCapsule(g, knobMin, by, knobMax - knobMin, 4, C_SLIDER_FILL);
            for (int knob : new int[]{knobMin, knobMax}) {
                drawCapsule(g, knob - 5, by - 2, 10, 10, 0x55000000);
                drawCapsule(g, knob - 5, by - 1, 10, 10, 0x1C000000);
                drawCapsule(g, knob - 5, by - 3, 10, 10, C_SLIDER_KNOB);
                drawCapsule(g, knob - 4, by - 3, 8, 1, 0x55FFFFFF);
            }

        } else if (s instanceof ModeSelectSetting m) {
            g.text(this.font, nice(m.getName()), x + 4, textY, C_TEXT_DIM, true);
            String val = m.getValue();
            g.text(this.font, nice(val), x + w - niceW(val) - 4, textY, C_TEXT, true);

        } else if (s instanceof InputSetting inp) {
            boolean active = inp == activeInput;
            String val = trimW(active ? inp.getValue() + "|" : inp.getValue(), w / 2);
            g.text(this.font, nice(inp.getName()), x + 4, textY, C_TEXT_DIM, true);
            g.text(this.font, nice(val), x + w - niceW(val) - 4, textY,
                    active ? C_TEXT : C_TEXT_DIM, active);
        }
    }

    private void drawScrollbar(GuiGraphicsExtractor g, int x, int y, int w, int h, int scroll, int total) {
        g.fill(x, y, x + w, y + h, C_SCROLLBAR);
        int thumbH = Math.max(16, h * h / total);
        int thumbY = y + (int)((h - thumbH) * (double)scroll / Math.max(1, total - h));
        g.fill(x, thumbY, x + w, thumbY + thumbH, C_SCROLLBAR_TH);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mx = event.x() * guiScale / uiScale;
        double my = event.y() * guiScale / uiScale;
        int btn = event.button();
        int gx = guiX(), gy = guiY();
        int gw = guiW(), gh = guiH();

        int catY = gy + 36;
        for (Category cat : Category.values()) {
            if (isIn(mx, my, gx, catY, SIDEBAR_W, CAT_H)) {
                activeTab = cat;
                settingsModule = null;
                moduleScroll = 0;
                return true;
            }
            catY += CAT_H;
        }

        if (settingsModule != null) {
            int px = gx + gw;
            int sw = settingsW();
            if (isIn(mx, my, px + sw - 22, gy + 6, 16, 16)) {
                settingsModule = null;
                settingsScroll = 0;
                return true;
            }
            int contentY = gy + 28;
            int sy = contentY + 8 - settingsScroll;
            for (Setting<?> s : settingsModule.getSettings()) {
                if (!s.isVisible()) continue;
                int sh = settingH(s);
                if (isIn(mx, my, px + 6, sy, sw - 12, sh)) {
                    handleSettingClick(s, px + 6, sw - 12, mx);
                    return true;
                }
                sy += sh + 4;
            }
        }

        List<Module> mods = ModuleManager.getInstance().getModulesByCategory(activeTab);
        int contentX = gx + SIDEBAR_W;
        int contentW = gw - SIDEBAR_W;
        int cardW = (contentW - CONTENT_PAD * 2 - CARD_GAP * (CARD_COLS - 1)) / CARD_COLS;

        for (int i = 0; i < mods.size(); i++) {
            Module mod = mods.get(i);
            int cardX = contentX + CONTENT_PAD + (i % CARD_COLS) * (cardW + CARD_GAP);
            int cardY = gy + CONTENT_PAD + (i / CARD_COLS) * (CARD_H + CARD_GAP) - moduleScroll;
            if (isIn(mx, my, cardX, cardY, cardW, CARD_H)) {
                if (btn == 0) mod.toggle();
                else if (btn == 1) {
                    if (settingsModule == mod) {
                        settingsModule = null;
                    } else if (hasVisibleSettings(mod)) {
                        settingsModule = mod;
                    }
                    settingsScroll = 0;
                }
                return true;
            }
        }

        activeInput = null;
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY) {
        mx = mx * guiScale / uiScale;
        my = my * guiScale / uiScale;
        int gx = guiX(), gy = guiY();
        int gw = guiW(), gh = guiH();
        int delta = scrollY > 0 ? -SCROLL_SPEED : SCROLL_SPEED;

        if (settingsModule != null) {
            int px = gx + gw;
            int sw = settingsW();
            if (isIn(mx, my, px, gy, sw, gh)) {
                settingsScroll = Math.max(0, settingsScroll + delta);
                return true;
            }
        }

        if (isIn(mx, my, gx + SIDEBAR_W, gy, gw - SIDEBAR_W, gh)) {
            moduleScroll = Math.max(0, moduleScroll + delta);
            return true;
        }

        return super.mouseScrolled(mx, my, scrollX, scrollY);
    }

    private void handleSettingClick(Setting<?> s, int barX, int barW, double mx) {
        if (s instanceof BooleanSetting b) {
            b.toggle();
        } else if (s instanceof ModeSelectSetting m) {
            m.cycle();
        } else if (s instanceof SliderSetting sl) {
            draggingSlider = true;
            activeSlider = sl;
            sliderBarX = barX + 4;
            sliderBarW = barW - 8;
            updateSlider(mx);
        } else if (s instanceof RangeSliderSetting rs) {
            sliderBarX = barX + 4;
            sliderBarW = barW - 8;
            activeRangeSlider = rs;
            double range = rs.getMax() - rs.getMin();
            double pctMin = (rs.getMinValue() - rs.getMin()) / range;
            double pctMax = (rs.getMaxValue() - rs.getMin()) / range;
            int knobMin = sliderBarX + (int)(sliderBarW * pctMin);
            int knobMax = sliderBarX + (int)(sliderBarW * pctMax);
            draggingRangeMax = Math.abs(mx - knobMax) <= Math.abs(mx - knobMin);
            updateRangeSlider(mx);
        } else if (s instanceof InputSetting inp) {
            activeInput = (activeInput == inp) ? null : inp;
        }
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        double mx = event.x() * guiScale / uiScale;
        if (draggingSlider && activeSlider != null) {
            updateSlider(mx);
            return true;
        }
        if (activeRangeSlider != null) {
            updateRangeSlider(mx);
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        draggingSlider = false;
        activeSlider = null;
        activeRangeSlider = null;
        return super.mouseReleased(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (activeInput != null) {
            if (event.key() == 259 && !activeInput.getValue().isEmpty()) {
                activeInput.setValue(activeInput.getValue().substring(0, activeInput.getValue().length() - 1));
                return true;
            }
            if (event.isEscape()) {
                activeInput = null;
                return true;
            }
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (activeInput != null) {
            if (event.isAllowedChatCharacter())
                activeInput.setValue(activeInput.getValue() + event.codepointAsString());
            return true;
        }
        return super.charTyped(event);
    }

    private void updateSlider(double mx) {
        double pct = Math.max(0, Math.min(1, (mx - sliderBarX) / sliderBarW));
        activeSlider.setValue((int) Math.round(activeSlider.getMin() + pct * (activeSlider.getMax() - activeSlider.getMin())));
    }

    private void updateRangeSlider(double mx) {
        double pct = Math.max(0, Math.min(1, (mx - sliderBarX) / sliderBarW));
        double v = activeRangeSlider.getMin() + pct * (activeRangeSlider.getMax() - activeRangeSlider.getMin());
        if (draggingRangeMax) activeRangeSlider.setMaxValue(v);
        else activeRangeSlider.setMinValue(v);
    }

    private int guiW() {
        return GUI_W;
    }

    private int guiH() {
        return GUI_H;
    }

    private int settingsW() {
        return GUI_SETTINGS_W;
    }

    private int guiX() {
        int total = settingsModule != null ? GUI_W + GUI_SETTINGS_W : GUI_W;
        return (viewW() - total) / 2;
    }

    private int guiY() {
        return (viewH() - GUI_H) / 2;
    }

    private boolean isIn(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    private String trimW(String text, int maxPx) {
        if (niceW(text) <= maxPx) return text;
        while (!text.isEmpty() && niceW(text + "…") > maxPx)
            text = text.substring(0, text.length() - 1);
        return text + "…";
    }

    private static final Style NICE_STYLE = Style.EMPTY.withFont(
            new FontDescription.Resource(Identifier.parse("fabricclient:nice")));

    private Component nice(String s) {
        return Component.literal(s).withStyle(NICE_STYLE);
    }

    private int niceW(String s) {
        return this.font.width(nice(s));
    }

    private void drawCapsule(GuiGraphicsExtractor g, int x, int y, int w, int h, int color) {
        if (w <= 0 || h <= 0) return;
        double r = h / 2.0;
        double rr = r * r;
        for (int i = 0; i < h; i++) {
            double dy = i + 0.5 - r;
            int off = (int) Math.round(Math.sqrt(Math.max(0.0, rr - dy * dy)));
            int sx = (int)(r) - off;
            int ex = w - sx;
            if (sx < ex) g.fill(x + sx, y + i, x + ex, y + i + 1, color);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
