package org.awesome.fabricclient.client.gui;

import net.minecraft.client.Minecraft;
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

    private static final int GUI_W          = 640;
    private static final int GUI_H          = 360;
    private static final int GUI_SETTINGS_W = 192;

    private static final int SIDEBAR_W   = 76;
    private static final int SIDEBAR_PAD = 8;
    private static final int CAT_H       = 24;

    private static final int CONTENT_PAD = 10;
    private static final int CARD_COLS   = 2;
    private static final int CARD_GAP    = 6;
    private static final int CARD_H      = 40;

    private static final int PILL_W      = 30;
    private static final int PILL_H      = 14;

    private static final int UI_SCALE = 2;
    private static final int FIT_PAD  = 12;

    private double guiScale = 1.0;
    private int    uiScale  = UI_SCALE;

    private int fbW() { return (int)(this.width  * guiScale); }
    private int fbH() { return (int)(this.height * guiScale); }

    private int viewW() { return (int)(fbW() / uiScale); }
    private int viewH() { return (int)(fbH() / uiScale); }

    private static final int   SCROLL_SPEED   = 10;

    private static final int C_BG           = 0xF2111620;
    private static final int C_BG_DARK      = 0xF00C1018;
    private static final int C_TAB_BAR      = 0xF0141A26;
    private static final int C_TAB_ACTIVE   = 0xF0192134;
    private static final int C_TAB_HOVER    = 0xBB18202E;
    private static final int C_TAB_TEXT     = 0xFFFFFFFF;
    private static final int C_TAB_TEXT_DIM = 0xFF8A99B5;
    private static final int C_BORDER       = 0xFF1A2640;
    private static final int C_CARD         = 0xCC131C2E;
    private static final int C_CARD_HOVER   = 0xCC1C2840;
    private static final int C_CARD_BORDER  = 0xFF1C2A42;
    private static final int C_CARD_SEL     = 0xCC1E2C46;
    private static final int C_CARD_SEL_BRD = 0xFF2E4E78;
    private static final int C_TEXT         = 0xFFF2F6FF;
    private static final int C_TEXT_DIM     = 0xFFB4C0D2;
    private static final int C_TEXT_DESC    = 0xFF94A4BC;
    private static final int C_ACCENT       = 0xFFE8701A;
    private static final int C_SET_BG       = 0xF00E1422;
    private static final int C_SET_BORDER   = 0xFF1E3050;
    private static final int C_SLIDER_BG    = 0xFF162030;
    private static final int C_SLIDER_FILL  = 0xFFE8701A;
    private static final int C_SLIDER_KNOB  = 0xFFFFFFFF;
    private static final int C_OVERLAY      = 0x88040810;
    private static final int C_SCROLLBAR    = 0xFF18243A;
    private static final int C_SCROLLBAR_TH = 0xFF3A5272;

    private Module.Category activeTab      = Module.Category.COMBAT;
    private Module          settingsModule = null;

    private int             moduleScroll   = 0;
    private int             settingsScroll = 0;

    private boolean             draggingSlider    = false;
    private SliderSetting       activeSlider      = null;
    private RangeSliderSetting  activeRangeSlider = null;
    private boolean             draggingRangeMax  = false;
    private int                 sliderBarX, sliderBarW;
    private InputSetting        activeInput       = null;

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
        int gw = GUI_W,  gh = GUI_H;

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
        for (Module.Category cat : Module.Category.values()) {
            boolean active  = cat == activeTab;
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

        int cardW  = (cw - CONTENT_PAD * 2 - CARD_GAP * (CARD_COLS - 1)) / CARD_COLS;
        int rows   = (mods.size() + CARD_COLS - 1) / CARD_COLS;
        int totalH = rows * (CARD_H + CARD_GAP) - CARD_GAP + CONTENT_PAD * 2;

        int maxScroll = Math.max(0, totalH - ch);
        moduleScroll  = Math.max(0, Math.min(moduleScroll, maxScroll));

        for (int i = 0; i < mods.size(); i++) {
            Module mod  = mods.get(i);
            int cardX   = cx + CONTENT_PAD + (i % CARD_COLS) * (cardW + CARD_GAP);
            int cardY   = cy + CONTENT_PAD + (i / CARD_COLS) * (CARD_H + CARD_GAP) - moduleScroll;
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
        int bg  = selected ? C_CARD_SEL     : (hov ? C_CARD_HOVER : C_CARD);
        int brd = selected ? C_CARD_SEL_BRD : C_CARD_BORDER;

        g.fill(x, y, x + w, y + CARD_H, bg);
        g.outline(x, y, w, CARD_H, brd);

        if (mod.isEnabled()) g.fill(x, y + 3, x + 2, y + CARD_H - 3, C_ACCENT);

        int nameColor = mod.isEnabled() ? C_TEXT : C_TEXT_DIM;
        g.text(this.font, nice(mod.getName()), x + 10, y + 9, nameColor, mod.isEnabled());

        String bind = "NONE";
        int tagX = x + 10 + niceW(mod.getName()) + 6;
        g.fill(tagX - 2, y + 7, tagX + niceW(bind) + 3, y + 19, 0x55101C30);
        g.text(this.font, nice(bind), tagX, y + 9, 0xFF6E7E96, false);

        String desc = mod.getDescription();
        if (desc != null && !desc.isEmpty()) {
            g.text(this.font, nice(trimW(desc, w - PILL_W - 22)), x + 10, y + 22, C_TEXT_DESC, false);
        }

        drawPill(g, mod.isEnabled(), x + w - PILL_W - 8, y + CARD_H / 2 - PILL_H / 2);
    }

    private void drawPill(GuiGraphicsExtractor g, boolean on, int x, int y) {
        int tw = PILL_W, th = PILL_H;

        if (on) {
            drawCapsule(g, x - 1, y - 1, tw + 2, th + 2, 0x33E8701A);
            drawCapsule(g, x - 2, y - 2, tw + 4, th + 4, 0x18E8701A);
        }

        drawCapsule(g, x,     y + 2, tw,     th, 0x44000000);
        drawCapsule(g, x + 1, y + 3, tw - 2, th, 0x18000000);

        int track = on ? 0xFFE8701A : 0xFF1B2434;
        drawCapsule(g, x, y, tw, th, track);

        if (!on) drawCapsule(g, x + 1, y + 1, tw - 2, th - 2, 0x18000000);

        drawCapsule(g, x + 2, y + 1, tw - 4, 1, on ? 0x66FFFFFF : 0x1AFFFFFF);
        drawCapsule(g, x + 2, y + th - 2, tw - 4, 1, 0x33000000);

        int kSize = th - 4;
        int kx = on ? x + tw - kSize - 2 : x + 2;
        int ky = y + 2;
        drawCapsule(g, kx, ky + 2, kSize, kSize, 0x66000000);
        drawCapsule(g, kx, ky + 1, kSize, kSize, 0x22000000);
        drawCapsule(g, kx, ky,     kSize, kSize, 0xFFFFFFFF);
        drawCapsule(g, kx + 1, ky, kSize - 2, 1, 0x55FFFFFF);
        drawCapsule(g, kx + 1, ky + kSize - 1, kSize - 2, 1, 0x22000000);
    }

    private void renderSettingsPanel(GuiGraphicsExtractor g, int px, int py, int gh, int mx, int my) {
        int sw = settingsW();

        g.fill(px + 3, py + 3, px + sw + 3, py + gh + 3, 0x44000000);
        g.fill(px, py, px + sw, py + gh, C_SET_BG);
        g.outline(px, py, sw, gh, C_SET_BORDER);

        g.fill(px, py, px + sw, py + 28, C_TAB_BAR);
        g.fill(px, py + 27, px + sw, py + 28, C_BORDER);
        g.fill(px, py + 26, px + sw, py + 28, C_ACCENT);
        g.text(this.font, nice(settingsModule.getName()), px + 8, py + 9, C_TEXT, true);

        int closeX = px + sw - 22, closeY = py + 6, closeS = 16;
        boolean closeHov = isIn(mx, my, closeX, closeY, closeS, closeS);
        if (closeHov) drawCapsule(g, closeX, closeY, closeS, closeS, 0x22FFFFFF);
        g.text(this.font, nice("×"), px + sw - 14, py + 9, closeHov ? C_TEXT : C_TEXT_DIM, closeHov);

        int contentY = py + 28;
        int contentH = gh - 28;

        List<Setting<?>> settings = settingsModule.getSettings();
        int totalSH = 8;
        int visibleCount = 0;
        for (Setting<?> s : settings) {
            if (!s.isVisible()) continue;
            totalSH += settingH(s) + 4;
            visibleCount++;
        }

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
            g.text(this.font, nice("No settings."), px + 8, contentY + 12, C_TEXT_DIM, false);
        }

        if (maxScroll > 0) drawScrollbar(g, px + sw - 5, contentY + 2, 4, contentH - 4, settingsScroll, totalSH);
    }

    private int settingH(Setting<?> s) {
        if (s instanceof SliderSetting)      return 42;
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
            double pct  = (sl.getValue() - sl.getMin()) / (sl.getMax() - sl.getMin());
            int bx = x + 4, bw = w - 8, by = y + 28;
            int knob = bx + (int)(bw * pct);
            String val = String.format("%.1f", sl.getValue());
            g.text(this.font, nice(sl.getName()), x + 4, sliderTextY, C_TEXT_DIM, true);
            g.text(this.font, nice(val), x + w - niceW(val) - 4, sliderTextY, C_TEXT, true);
            drawCapsule(g, bx, by,     bw,        4, C_SLIDER_BG);
            drawCapsule(g, bx, by,     knob - bx, 4, C_SLIDER_FILL);
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
            drawCapsule(g, bx,      by, bw,                   4, C_SLIDER_BG);
            drawCapsule(g, knobMin, by, knobMax - knobMin,    4, C_SLIDER_FILL);
            for (int knob : new int[]{knobMin, knobMax}) {
                drawCapsule(g, knob - 5, by - 2, 10, 10, 0x55000000);
                drawCapsule(g, knob - 5, by - 1, 10, 10, 0x1C000000);
                drawCapsule(g, knob - 5, by - 3, 10, 10, C_SLIDER_KNOB);
                drawCapsule(g, knob - 4, by - 3,  8,  1, 0x55FFFFFF);
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
        for (Module.Category cat : Module.Category.values()) {
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

        List<Module> mods  = ModuleManager.getInstance().getModulesByCategory(activeTab);
        int contentX = gx + SIDEBAR_W;
        int contentW = gw - SIDEBAR_W;
        int cardW    = (contentW - CONTENT_PAD * 2 - CARD_GAP * (CARD_COLS - 1)) / CARD_COLS;

        for (int i = 0; i < mods.size(); i++) {
            Module mod   = mods.get(i);
            int    cardX = contentX + CONTENT_PAD + (i % CARD_COLS) * (cardW + CARD_GAP);
            int    cardY = gy + CONTENT_PAD + (i / CARD_COLS) * (CARD_H + CARD_GAP) - moduleScroll;
            if (isIn(mx, my, cardX, cardY, cardW, CARD_H)) {
                if (btn == 0) mod.toggle();
                else if (btn == 1) {
                    settingsModule = (settingsModule == mod) ? null : mod;
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
        if (s instanceof BooleanSetting b)         b.toggle();
        else if (s instanceof ModeSelectSetting m) m.cycle();
        else if (s instanceof SliderSetting sl) {
            draggingSlider = true;
            activeSlider   = sl;
            sliderBarX     = barX + 4;
            sliderBarW     = barW - 8;
            updateSlider(mx);
        } else if (s instanceof RangeSliderSetting rs) {
            sliderBarX        = barX + 4;
            sliderBarW        = barW - 8;
            activeRangeSlider = rs;
            double range = rs.getMax() - rs.getMin();
            double pctMin = (rs.getMinValue() - rs.getMin()) / range;
            double pctMax = (rs.getMaxValue() - rs.getMin()) / range;
            int knobMin = sliderBarX + (int)(sliderBarW * pctMin);
            int knobMax = sliderBarX + (int)(sliderBarW * pctMax);
            draggingRangeMax = Math.abs(mx - knobMax) <= Math.abs(mx - knobMin);
            updateRangeSlider(mx);
        } else if (s instanceof InputSetting inp)
            activeInput = (activeInput == inp) ? null : inp;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        double mx = event.x() * guiScale / uiScale;
        if (draggingSlider && activeSlider != null)        { updateSlider(mx);      return true; }
        if (activeRangeSlider != null)                     { updateRangeSlider(mx); return true; }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        draggingSlider    = false;
        activeSlider      = null;
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
            if (event.isEscape()) { activeInput = null; return true; }
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
        activeSlider.setValue(activeSlider.getMin() + pct * (activeSlider.getMax() - activeSlider.getMin()));
    }

    private void updateRangeSlider(double mx) {
        double pct = Math.max(0, Math.min(1, (mx - sliderBarX) / sliderBarW));
        double v = activeRangeSlider.getMin() + pct * (activeRangeSlider.getMax() - activeRangeSlider.getMin());
        if (draggingRangeMax) activeRangeSlider.setMaxValue(v);
        else                  activeRangeSlider.setMinValue(v);
    }

    private int guiW() { return GUI_W; }
    private int guiH() { return GUI_H; }

    private int settingsW() { return GUI_SETTINGS_W; }

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

    private Component nice(String s)  { return Component.literal(s).withStyle(NICE_STYLE); }
    private int       niceW(String s) { return this.font.width(nice(s)); }

    private void drawCapsule(GuiGraphicsExtractor g, int x, int y, int w, int h, int color) {
        if (w <= 0 || h <= 0) return;
        double r = h / 2.0;
        double rr = r * r;
        int alpha = (color >>> 24) & 0xFF;
        int rgb   = color & 0x00FFFFFF;
        for (int i = 0; i < h; i++) {
            double dy = i + 0.5 - r;
            double off = Math.sqrt(Math.max(0.0, rr - dy * dy));
            double leftEdge  = r - off;
            double rightEdge = w - (r - off);
            int sx = (int)Math.ceil(leftEdge);
            int ex = (int)Math.floor(rightEdge);
            if (sx < ex) g.fill(x + sx, y + i, x + ex, y + i + 1, color);
            if (sx > leftEdge) {
                int a = (int)(alpha * (sx - leftEdge));
                if (a > 0) g.fill(x + sx - 1, y + i, x + sx, y + i + 1, (a << 24) | rgb);
            }
            if (ex < rightEdge) {
                int a = (int)(alpha * (rightEdge - ex));
                if (a > 0) g.fill(x + ex, y + i, x + ex + 1, y + i + 1, (a << 24) | rgb);
            }
        }
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
