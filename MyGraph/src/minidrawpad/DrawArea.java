package minidrawpad;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.event.MouseMotionAdapter;

public class DrawArea extends JPanel{
    DrawPad drawpad =null;
    Drawing[] itemList =new Drawing[5000]; //绘制图形类

    private int currentChoice = 4;//设置默认基本图形状态为线段，控制画什么图形
    int index = 0;//当前已经绘制的图形数目
    private Color color = Color.black;//当前画笔的颜色
    int R,G,B;//用来存放当前颜色的彩值
    int f1,f2;//用来存放当前字体的风格
    String stytle ;//存放当前字体
    float stroke = 1.0f;//设置画笔的粗细 ，默认的是 1.0

    Drawing itemChoose;//当前选中图形

    //距离函数
    double lineSpace(int x1,int y1,int x2,int y2){
        double lineLength=0;
        lineLength=Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2)* (y1 - y2));
        return lineLength;
    }

    //到线段最短距离函数
    double pointToLine(int x1, int y1, int x2, int y2, int x0, int y0){
        double space = 0;
        double a, b, c;
        a = lineSpace(x1, y1, x2, y2);// 线段的长度
        b = lineSpace(x1, y1, x0, y0);// (x1,y1)到点的距离
        c = lineSpace(x2, y2, x0, y0);// (x2,y2)到点的距离

        if (c <= 0.000001 || b <= 0.000001) {
            space = 0;
            return space;
        }
        if (a <= 0.000001) {
            space = b;
            return space;
        }
        if (c * c >= a * a + b * b) {
            space = b;
            return space;
        }
        if (b * b >= a * a + c * c) {
            space = c;
            return space;
        }
        double p = (a + b + c) / 2;// 半周长
        double s = Math.sqrt(p * (p - a) * (p - b) * (p - c));// 海伦公式求面积
        space = 2 * s / a;// 返回点到线的距离（利用三角形面积公式求高）
        return space;
    }

    //是否在矩形内
    boolean pointToRect(int x1, int y1, int x2, int y2, int x0, int y0){
        if(x0>=Math.min(x1,x2)&&x0<=(Math.min(x1,x2)+Math.abs(x1-x2))&&y0>=Math.min(y1,y2)&&y0<=(Math.min(y1,y2)+Math.abs(y1-y2))) {
            return true;
        }
        else{
            return false;
        }
    }

    //是否在圆内
    boolean pointToCircle(int x1, int y1, int x2, int y2, int x0, int y0){
        int r=Math.max(Math.abs(x1-x2),Math.abs(y1-y2))/2;//半径
        int x_c=Math.min(x1,x2)+r;
        int y_c=Math.min(y1,y2)+r;

        if(lineSpace(x_c,y_c,x0,y0)<=r){
            return true;
        }
        else {
            return false;
        }
    }

    DrawArea(DrawPad dp) {
        drawpad = dp;
        // 把鼠标设置成十字形
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        // setCursor 设置鼠标的形状 ，getPredefinedCursor()返回一个具有指定类型的光标的对象
        setBackground(Color.white);// 设置绘制区的背景是白色
        addMouseListener(new MouseA());// 添加鼠标事件
        addMouseMotionListener(new MouseB());
        createNewitem();

    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;//定义随笔画
        int  j = 0;

        //System.out.println(index);
        while(j<index)
        {
            draw(g2d,itemList[j]);
            j++;

        }

    }
    void draw(Graphics2D g2d , Drawing i)
    {
        i.draw(g2d);//将画笔传到个各类的子类中，用来完成各自的绘图
    }


    void createNewitem(){
        if(currentChoice==14){//字体的输入光标相应的设置为文本输入格式
            setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        }
        else {
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }
        switch (currentChoice){
            case 4: itemList[index] = new Line();break;
            case 5: itemList[index] = new Rect();break;
            case 9: itemList[index] = new Circle();break;
            case 13: if(index>0){index--;}break;//撤回功能
            case 14: itemList[index] = new Word();break;

        }

        if(currentChoice!=13&&currentChoice!=11){
            itemList[index].type = currentChoice;
            itemList[index].R = R;
            itemList[index].G = G;
            itemList[index].B = B;
            itemList[index].stroke = stroke;
        }

    }

    public void setIndex(int x){//设置index的接口
        index = x;
    }
    public int getIndex(){//设置index的接口
        return index ;
    }
    public void setColor(Color color)//设置颜色的值
    {
        this.color = color;
    }
    public void setStroke(float f)//设置画笔粗细的接口
    {
        stroke = f;
    }

    public void chooseColor()//选择当前颜色
    {
        color = JColorChooser.showDialog(drawpad, "请选择颜色", color);
        try {
            R = color.getRed();
            G = color.getGreen();
            B = color.getBlue();
        } catch (Exception e) {
            R = 0;
            G = 0;
            B = 0;
        }
        itemList[index].R = R;
        itemList[index].G = G;
        itemList[index].B = B;
    }

    public void setStroke()//画笔粗细的调整
    {
        String input ;
        input = JOptionPane.showInputDialog("请输入画笔的粗细( >0 )");
        try {
            stroke = Float.parseFloat(input);

        } catch (Exception e) {
            stroke = 1.0f;

        }itemList[index].stroke = stroke;

    }
    public void setCurrentChoice(int i )//文字的输入
    {
        //currentChoice = i;
        if(i==3) {
            currentChoice=4;
        }
        else if(i==4){
            currentChoice=5;
        }
        else if(i==5){
            currentChoice=9;
        }
        else if(i==6){
            currentChoice=13;
        }
        else if(i==14){
            currentChoice=14;
        }
        else if(i==10){
            currentChoice=10;
        }
        else if(i==11){
            currentChoice=11;
        }
    }

    public void setFont(int  i,int font)//设置字体
    {
        if(i == 1)
        {
            f1 = font;
        }
        else
            f2 = font;
    }

    // TODO 鼠标事件MouseA类继承了MouseAdapter
    //用来完成鼠标的响应事件的操作（鼠标的按下、释放、单击、移动、拖动、何时进入一个组件、何时退出、何时滚动鼠标滚轮 )
    class MouseA extends MouseAdapter
    {

        @Override
        public void mouseEntered(MouseEvent me) {
            // TODO 鼠标进入
            drawpad.setStratBar("鼠标进入在：["+me.getX()+" ,"+me.getY()+"]");
        }
        @Override
        public void mouseExited(MouseEvent me) {
            // TODO 鼠标退出
            drawpad.setStratBar("鼠标退出在：["+me.getX()+" ,"+me.getY()+"]");
        }
        @Override
        public void mousePressed(MouseEvent me) {
            // TODO 鼠标按下
            drawpad.setStratBar("鼠标按下在：["+me.getX()+" ,"+me.getY()+"]");//设置状态栏提示

            if(currentChoice!=10&&currentChoice!=11) {
                itemList[index].x1 = itemList[index].x2 = me.getX();
                itemList[index].y1 = itemList[index].y2 = me.getY();
            }
            //如果当前选择为随笔画或橡皮擦 ，则进行下面的操作
//            if(currentChoice == 3||currentChoice ==13){
//                itemList[index].x1 = itemList[index].x2 = me.getX();
//                itemList[index].y1 = itemList[index].y2 = me.getY();
//                index++;
//                createNewitem();//创建新的图形的基本单元对象
//            }
            //当前选择撤销功能

            //选择图形
            if(currentChoice==10){
                //System.out.println("nmb10");
                itemChoose = new Drawing();
                for(int i=0;i<index;i++){
                    if(itemList[i].type==4){
                        //System.out.println("进来了！！！"+index);
                        if(pointToLine(itemList[i].x1,itemList[i].y1,itemList[i].x2,itemList[i].y2,me.getX(),me.getY())<10)
                        {
                            System.out.println("当前选中线段");
                            itemChoose.x1=itemList[i].x1;
                            itemChoose.x2=itemList[i].x2;
                            itemChoose.y1=itemList[i].y1;
                            itemChoose.y2=itemList[i].y2;
                            itemChoose.R=itemList[i].R;
                            itemChoose.G=itemList[i].G;
                            itemChoose.B=itemList[i].B;
                            itemChoose.stroke=itemList[i].stroke;
                            itemChoose.type=itemList[i].type;

                            break;
                        }
                    }//直线
                    else if(itemList[i].type==5){
                        if(pointToRect(itemList[i].x1,itemList[i].y1,itemList[i].x2,itemList[i].y2,me.getX(),me.getY())==true)
                        {
                            System.out.println("当前选中矩形");
                            itemChoose.x1=itemList[i].x1;
                            itemChoose.x2=itemList[i].x2;
                            itemChoose.y1=itemList[i].y1;
                            itemChoose.y2=itemList[i].y2;
                            itemChoose.R=itemList[i].R;
                            itemChoose.G=itemList[i].G;
                            itemChoose.B=itemList[i].B;
                            itemChoose.stroke=itemList[i].stroke;
                            itemChoose.type=itemList[i].type;

                            break;
                        }
                    }//矩形
                    else if(itemList[i].type==9){
                        if(pointToCircle(itemList[i].x1,itemList[i].y1,itemList[i].x2,itemList[i].y2,me.getX(),me.getY())==true)
                        {
                            System.out.println("当前选中圆");
                            itemChoose.x1=itemList[i].x1;
                            itemChoose.x2=itemList[i].x2;
                            itemChoose.y1=itemList[i].y1;
                            itemChoose.y2=itemList[i].y2;
                            itemChoose.R=itemList[i].R;
                            itemChoose.G=itemList[i].G;
                            itemChoose.B=itemList[i].B;
                            itemChoose.stroke=itemList[i].stroke;
                            itemChoose.type=itemList[i].type;

                            break;
                        }
                    }//圆
                }

            }

            //复制
            if(currentChoice==11){
//                System.out.println("nmb11");
//                System.out.println(itemChoose.x1+ " " +itemChoose.y1);
//                System.out.println(itemChoose.type);

                switch (itemChoose.type){
                    case 4:itemList[index]=new Line();break;
                    case 5:itemList[index]=new Rect();break;
                    case 9:itemList[index]=new Circle();break;
                }

                itemList[index].x1=me.getX();
                itemList[index].y1=me.getY();

                if(itemChoose.type==5||itemChoose.type==9) {
                    itemList[index].x2 = me.getX() + Math.abs(itemChoose.x1 - itemChoose.x2);
                    itemList[index].y2 = me.getY() + Math.abs(itemChoose.y1 - itemChoose.y2);
                }
                else if(itemChoose.type==4){
                    if(itemChoose.x1<itemChoose.x2&&itemChoose.y1<itemChoose.y2){
                        itemList[index].x2 = me.getX() + Math.abs(itemChoose.x1 - itemChoose.x2);
                        itemList[index].y2 = me.getY() + Math.abs(itemChoose.y1 - itemChoose.y2);
                    }
                    else if(itemChoose.x1>itemChoose.x2&&itemChoose.y1>itemChoose.y2){
                        itemList[index].x2 = me.getX() - Math.abs(itemChoose.x1 - itemChoose.x2);
                        itemList[index].y2 = me.getY() - Math.abs(itemChoose.y1 - itemChoose.y2);
                    }
                    else if(itemChoose.x1>itemChoose.x2&&itemChoose.y1<itemChoose.y2){
                        itemList[index].x2 = me.getX() - Math.abs(itemChoose.x1 - itemChoose.x2);
                        itemList[index].y2 = me.getY() + Math.abs(itemChoose.y1 - itemChoose.y2);
                    }
                    else if(itemChoose.x1<itemChoose.x2&&itemChoose.y1>itemChoose.y2){
                        itemList[index].x2 = me.getX() + Math.abs(itemChoose.x1 - itemChoose.x2);
                        itemList[index].y2 = me.getY() - Math.abs(itemChoose.y1 - itemChoose.y2);
                    }
                }

                itemList[index].R=itemChoose.R;
                itemList[index].G=itemChoose.G;
                itemList[index].B=itemChoose.B;

                itemList[index].stroke=itemChoose.stroke;
                itemList[index].type=itemChoose.type;

                index++;
                currentChoice=11;
                //createNewitem();
                repaint();
            }
            //如果选择图形的文字输入，则进行下面的操作
            if(currentChoice == 14){
                itemList[index].x1 = me.getX();
                itemList[index].y1 = me.getY();
                String input ;
                input = JOptionPane.showInputDialog("请输入你要写入的文字！");
                itemList[index].s1 = input;
                itemList[index].x2 = f1;
                itemList[index].y2 = f2;
                itemList[index].s2 = stytle;

                index++;
                currentChoice = 14;
                createNewitem();//创建新的图形的基本单元对象
                repaint();
            }

        }
        @Override
        public void mouseReleased(MouseEvent me) {
            // TODO 鼠标松开
            drawpad.setStratBar("鼠标松开在：["+me.getX()+" ,"+me.getY()+"]");
//            if(currentChoice == 3||currentChoice ==13){
//                itemList[index].x1 = me.getX();
//                itemList[index].y1 = me.getY();
//            }
            if(currentChoice!=10&&currentChoice!=11)
            {
                itemList[index].x2 = me.getX();
                itemList[index].y2 = me.getY();
                repaint();
                index++;

                createNewitem();//创建新的图形的基本单元对象}
            }
        }
    }
    // 鼠标事件MouseB继承了MouseMotionAdapter
    // 用来处理鼠标的滚动与拖动

    class MouseB extends MouseMotionAdapter {
        public void mouseDragged(MouseEvent me)//鼠标的拖动
        {
            drawpad.setStratBar("鼠标拖动在：["+me.getX()+" ,"+me.getY()+"]");
//            if(currentChoice == 3||currentChoice ==13){
//                itemList[index-1].x1 = itemList[index].x2 = itemList[index].x1 =me.getX();
//                itemList[index-1].y1 = itemList[index].y2 = itemList[index].y1 = me.getY();
//                index++;
//                createNewitem();//创建新的图形的基本单元对象
//            }
//            else
//            {
//                itemList[index].x2 = me.getX();
//                itemList[index].y2 = me.getY();
//            }
            if(currentChoice!=10&&currentChoice!=11) {
                itemList[index].x2 = me.getX();
                itemList[index].y2 = me.getY();
                repaint();
            }
        }
        public void mouseMoved(MouseEvent me)//鼠标的移动
        {
            drawpad.setStratBar("鼠标移动在：["+me.getX()+" ,"+me.getY()+"]");
        }
    }
}
