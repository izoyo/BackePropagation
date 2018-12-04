import java.io.*;
import java.util.*;


class cv{//全局变量
    static int lenAttr = 4;//属性数量
}
class Flower{
    double[] attr;//属性
    int type;//种类
    Flower(){
        attr = new double[cv.lenAttr];
    }
}

class tGroup{
    int num;//成员数
    Flower[] member;//成员
    tGroup(){
        member = new Flower[200];
        num = 0;
    }
    //预测鸢尾花
    public void preF() throws IOException{
        try {
            DataInputStream in = new DataInputStream(new FileInputStream("G:\\data\\data.txt"));
            BufferedReader d  = new BufferedReader(new InputStreamReader(in));
            String rLine;
            this.num = 0;

            while((rLine = d.readLine()) != null){
                String[] aa = rLine.split(",");//分割出属性
                if(aa.length == cv.lenAttr+1){//因为数据多了个类别
                    this.member[this.num] = new Flower();
                    for (int i = 0; i < cv.lenAttr; i++) {
                        this.member[this.num].attr[i] = Double.valueOf(aa[i]);
                    }
                    switch(aa[cv.lenAttr]){
                        case "Iris-versicolor":
                            this.member[this.num].type = 1;
                            break;
                        case "Iris-virginica":
                            this.member[this.num].type = 2;
                            break;
                        case "Iris-setosa":
                            this.member[this.num].type = 0;
                            break;
                    }
                    this.num += 1;
                }
            }
            d.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        //导入书上的例子
//        this.member[0] = new Flower();
//        this.member[0].attr[0] = 1;
//        this.member[0].attr[1] = 1;
//
//        this.num += 1;


        System.out.println("导入数据："+this.num);

//        double smax, smin;
//        //归一化，然而不知道为什么并没有什么用！！！！
//        for (int j = 0; j < cv.lenAttr; j++) {
//            smax = smin = 0;
//            for (int i = 0; i < this.num; i++) {
//                smax = (this.member[i].attr[j] > smax)? this.member[i].attr[j]:smax;
//                smin = (this.member[i].attr[j] < smin)? this.member[i].attr[j]:smin;
//            }
//            for (int i = 0; i < this.num; i++) {
//                this.member[i].attr[j] = (this.member[i].attr[j]-smin)/(smax-smin);
//            }
//        }


        int inputNum = 4;//输入层数量
        int hideNum = 4;//隐含层数量
        int outNum = 3;//输出层数量
        int trainNum = 0;//训练次数
        Network bp = new Network(inputNum, hideNum, outNum, 0.1);

        double[] outValue;
        //开始训练
        for (int i = 0; i != 1000; i++) {
            for (int j = 0; j < this.num; j++) {

                outValue = new double[outNum];
                for (int k = 0; k < outValue.length; k++) {
                    outValue[k] = 0;
                }
                outValue[this.member[j].type] = 1;

                double[] inAttr = new double[inputNum];

                for (int k = 0; k < inAttr.length; k++) {
                    inAttr[k] = this.member[j].attr[k];
                }

                trainNum += bp.bpTrain(inAttr, outValue);
            }
        }
        System.out.println("\n训练次数："+trainNum);
        bp.printWE();
        System.out.println("—————————————————开始预测——————————————————————");
        //System.in.read();
        //待测试的数据
        double[][] testGroup = {
                {5.1,3.5,1.4,0.2},
                {4.9,3,1.4,0.2},
                {5.0,2.0,3.5,1.0},
                {6.1,2.8,4.7,1.2},
                {6.7,2.5,5.8,1.8},
                {7.2,3.2,6,1.8}
        };
        for (int o = 0; o < testGroup.length; o++) {

            double[] binary = new double[inputNum];//传入输出值
            binary[0] = testGroup[o][0];
            binary[1] = testGroup[o][1];
            binary[2] = testGroup[o][2];
            binary[3] = testGroup[o][3];

            double[] result = new double[outNum];

            bp.preResult(binary,result);//开始预测，返回结果：result

            double max = 0;
            int resultID = -1;
            for (int i = 0; i != result.length; i++) {
                System.out.print("【"+result[i]+"】");
                if (result[i] > max) {
                    max = result[i];
                    resultID = i;
                }
            }

            switch (resultID) {
                case 0:
                    System.out.println("Iris-setosa");
                    break;
                case 1:
                    System.out.println("Iris-versicolor");
                    break;
                case 2:
                    System.out.println("Iris-virginica");
                    break;
            }
        }
    }

    //预测红酒
    public void preW() throws IOException{
        try {
            DataInputStream in = new DataInputStream(new FileInputStream("G:\\红酒数据\\wine.data"));
            BufferedReader d  = new BufferedReader(new InputStreamReader(in));
            String rLine;
            this.num = 0;

            while((rLine = d.readLine()) != null){
                String[] aa = rLine.split(",");//分割出属性
                if(aa.length == cv.lenAttr+1){//因为数据多了个类别
                    this.member[this.num] = new Flower();
                    for (int i = 0; i < cv.lenAttr; i++) {
                        this.member[this.num].attr[i] = Double.valueOf(aa[i+1]);
                    }
                    switch(aa[0]){
                        case "1":
                            this.member[this.num].type = 0;
                            break;
                        case "2":
                            this.member[this.num].type = 1;
                            break;
                        case "3":
                            this.member[this.num].type = 2;
                            break;
                    }
                    this.num += 1;
                }
            }
            d.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        System.out.println("导入数据："+this.num);


        int inputNum = cv.lenAttr;//输入层数量 = 属性数量
        int hideNum = 30;//隐含层数量
        int outNum = 3;//输出层数量
        int trainNum = 0;//训练次数
        Network bp = new Network(inputNum, hideNum, outNum, 0.1);

        double[] outValue;
        for (int i = 0; i != 10; i++) {
            for (int j = 0; j < this.num; j++) {

                outValue = new double[outNum];
                for (int k = 0; k < outValue.length; k++) {
                    outValue[k] = 0;
                }
                outValue[this.member[j].type] = 1;

                double[] inAttr = new double[inputNum];

                for (int k = 0; k < inAttr.length; k++) {
                    inAttr[k] = this.member[j].attr[k];
                }

                trainNum += bp.bpTrain(inAttr, outValue);
            }
        }
        System.out.println("\n训练次数："+trainNum);
        bp.printWE();
        System.out.println("————————————————————————————————————————");
        //System.in.read();
        double[][] testGroup = {
                {13.71,5.65,2.45,20.5,95,1.68,.61,.52,1.06,7.7,.64,1.74,740}
        };
        for (int o = 0; o < testGroup.length; o++) {

            double[] binary = new double[inputNum];
            for (int i = 0; i < 13; i++) {
                binary[i] = testGroup[o][i];
            }


            double[] result = new double[outNum];
            bp.preResult(binary,result);

            double max = 0;
            int resultID = -1;
            for (int i = 0; i != result.length; i++) {
                System.out.print("【"+result[i]+"】");
                if (result[i] > max) {
                    max = result[i];
                    resultID = i;
                }
            }

            switch (resultID) {
                case 0:
                    System.out.println("1");
                    break;
                case 1:
                    System.out.println("2");
                    break;
                case 2:
                    System.out.println("3");
                    break;
            }
        }
    }
}


public class Main {

    public static void main(String[] args) throws IOException  {
//        Random r = new Random();
//        for (int i = 1; i < 100; i++) {
//            System.out.println(r.nextDouble() * 4.8 -2.4);
//        }
//
//        System.out.println(Double.valueOf(".02"));
//        System.in.read();

        tGroup tFlower = new tGroup();
        tFlower.preF();//cv.lenAttr = 4
//        tFlower.preW();//cv.lenAttr = 13



    }

}
