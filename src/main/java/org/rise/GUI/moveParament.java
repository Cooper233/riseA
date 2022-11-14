package org.rise.GUI;

import lk.vexview.gui.OpenedVexGui;
import lk.vexview.gui.components.VexComponents;
import lk.vexview.gui.components.VexImage;

class moveParament implements Runnable {
    public OpenedVexGui gui;
    public VexComponents components;
    public double a;
    public double c;
    public boolean isLeft;
    public int times;
    public boolean del;

    @Override
    public void run() {

        for (int i = 0; i < times; i++) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int d = (int) (a * Math.pow(i * 0.01, 2) + c);
            if (isLeft) {
                d *= -1;
                d = Math.min(d, -1);
            } else {
                d = Math.max(d, 1);
            }
            if (components instanceof VexImage) {
                VexImage a = (VexImage) components;
                a.setX(components.getX() + d);
                gui.addDynamicComponent(a);
                a.setX(a.getX() - d);
                gui.removeDynamicComponent(a);
                a.setX(a.getX() + d);
            }
        }
        if (del) {
            if (components instanceof VexImage) {
                VexImage a = (VexImage) components;
                gui.removeDynamicComponent(a);
            }
        }
    }

    public moveParament(OpenedVexGui gui, VexComponents components, double a, double c, boolean isLeft, int times) {
        this(gui, components, a, c, isLeft, times, false);
    }

    public moveParament(OpenedVexGui gui, VexComponents components, double a, double c, boolean isLeft, int times, boolean del) {
        this.gui = gui;
        this.components = components;
        this.a = a;
        this.c = c;
        this.isLeft = isLeft;
        this.times = times;
        this.del = del;
    }
}
