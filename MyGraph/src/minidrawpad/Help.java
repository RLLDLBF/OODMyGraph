package minidrawpad;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
//帮助菜单功能的事项类
public class Help extends JFrame {
    private DrawPad  drawpad = null;
    Help(DrawPad dp)
    {
        drawpad = dp;
    }

    public void MainHeip()
    {
        JOptionPane.showMessageDialog(this,"Java绘图帮助文档！","Java绘图",JOptionPane.WARNING_MESSAGE);
    }
    public void AboutBook()
    {
        JOptionPane.showMessageDialog(drawpad,"Java绘图"+"    版本: 1.0"
                +"    作者:  卫 昱 阳"
                +"    时间:  2018/6/13","Java绘图",JOptionPane.WARNING_MESSAGE);
    }
}