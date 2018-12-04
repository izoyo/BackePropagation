import java.util.Random;
import java.io.*;

public class Network {
    double[] input;//输入层
    //double[] output;//实际输出
    double[] exceptOut;//期望输出

    //double[] hideX; 隐含层输入就是输入层
    double[][] hideW;//隐含层权重
    double[] hideO;//隐含层阈值
    double[] hideE;//隐含层误差梯度

    double[] outX;//输出层输入\隐含层输出
    double[][] outW;//输出层权重
    double[] outO;//输出层阈值
    double[] outE;//输出层误差梯度

    double rate;//学习速率

    /**
     * 输出权重和误差
     */
    public void printWE(){
        System.out.println("\n隐含层权重：");
        for (int i = 0; i < hideW.length; i++) {
            for (int j = 0; j < hideW[i].length; j++) {
                System.out.print("  w"+i+","+j+":"+hideW[i][j]);
            }
            System.out.println("");
        }
        System.out.println("隐含层阈值：");
        for (int i = 0; i < hideO.length; i++) {
            System.out.print("  O"+i+":"+hideO[i]);
        }
        System.out.println("\n隐含层误差梯度：");
        for (int i = 0; i < hideE.length; i++) {
            System.out.print("  E"+i+":"+hideE[i]);
        }
        System.out.println("\n\n输出层权重：");
        for (int i = 0; i < outW.length; i++) {
            for (int j = 0; j < outW[i].length; j++) {
                System.out.print("  w"+i+","+j+":"+outW[i][j]);
            }
            System.out.println(" ");
        }
        System.out.println("输出层阈值：");
        for (int i = 0; i < outO.length; i++) {
            System.out.print("  O"+i+":"+outO[i]);
        }
        System.out.println("\n输出层误差梯度：");
        for (int i = 0; i < outE.length; i++) {
            System.out.print("  E"+i+":"+outE[i]);
        }
        System.out.println("\n");
    }

    /**
     * 初始化
     * @param input_num 输入层数量
     * @param hide_num 隐含层数量
     * @param out_num 输出层数量
     * @param rate 学习效率
     */
    Network(int input_num, int hide_num, int out_num, double rate) {

        // 隐含层的输入权重和误差
        input = new double[input_num];
        hideW = new double[hide_num][input_num];
        hideO = new double[hide_num];
        hideE = new double[hide_num];

        // 输出层的输入权重和误差
        outX = new double[hide_num];
        outW = new double[out_num][hide_num];
        outO = new double[out_num];
        outE = new double[out_num];

        //期望输出
        exceptOut = new double[out_num];

        // 学习速率
        this.rate = rate;

        //初始化权重
        initWeight(hideW, hideO);
        initWeight(outW, outO);

        //导入书上的例子
//        hideW[0][0]=0.5;
//        hideW[0][1]=0.4;
//        hideW[1][0]=0.9;
//        hideW[1][1]=1.0;
//        outW[0][0]=-1.2;
//        outW[0][1]=1.1;
//        hideO[0]=0.8;
//        hideO[1]=-0.1;
//        outO[0]=0.3;
    }

    /**
     * 初始化权重、阈值
     * @param w 一个二维的权重数组
     */
    private void initWeight(double[][] w, double[] o) {
        Random r = new Random();
        double f = 2.4 / input.length;
        for (int i = 0; i < w.length; i++)
            for (int j = 0; j < w[i].length; j++) {
                w[i][j] = (r.nextDouble() * f * 2 - f);
                //w[i][j] = 0;
            }
        for (int i = 0; i < hideO.length; i++) {
            hideO[i] = (r.nextDouble() * f * 2 - f);
        }


    }


    /**
     * 导入训练集
     * @param data 数据集
     * @param y 期望输出
     * @return
     */
    public int bpTrain(double[] data, double[] y) throws IOException  {
        //传入输入值
        System.arraycopy(data, 0, input, 0, data.length);
        //传入期望输出
        System.arraycopy(y, 0, exceptOut, 0, y.length);

//        System.out.print("\n输入：");
//        for (int i = 0; i < input.length; i++) {
//            System.out.print("【"+input[i]+"】");
//        }
//        System.out.println("  期望输出：");
//        for (int i = 0; i < exceptOut.length; i++) {
//            System.out.print("【"+exceptOut[i]+"】");
//        }
        double allErr = 0;//误差
        int trainNum = 0;//训练次数
        double[] acOut;
        do {
            acOut = new double[hideW.length];//临时输出
//            printWE();
            forword(input, acOut);//正向传播
            allErr = 0;//计算当前误差
            for (int i = 0; i < exceptOut.length; i++) {
                allErr += (exceptOut[i] - acOut[i]) * (exceptOut[i] - acOut[i]);
            }
            backword(acOut);//反向传播
            trainNum++;

//            System.out.println("【" + trainNum + "】");
//            printWE();
//            System.in.read();

            if (trainNum > 10000) break;//避免死循环
            //System.out.println("\n");

        }while (allErr > 0.001);
        //}while (false);
        //System.out.println(trainNum+"次训练后，误差^2："+allErr);

        return trainNum;
    }

    /**
     * 正向传播
     * @param in 当前层输入
     * @param out 临时输出
     */
    public void forword(double[] in ,double[] out){

//        System.out.println("\n———————————隐含层输出—————————————————————");
        getNetOutput(in, hideW, outX, hideO);//获取隐含层的输出outX
//        System.out.println("———————————输出层输出—————————————————————");
        getNetOutput(outX, outW, out, outO);//获取输出层的输出out
//        System.out.println("———————————实际输出：期望输出—————————————————————");

//        调试输出值和期望值
//        for (int i = 0; i < exceptOut.length; i++) {
//            System.out.println(out[i] + "  " + exceptOut[i]);
//        }
    }
    /**
     * 获取当前层的输出
     * @param x 输入
     * @param w 权值
     * @param y 当前层的输出数组
     * @param o 当前层阈值
     */
    private void getNetOutput(double[] x, double[][] w, double[] y, double[] o) {

        double z;
        for (int i = 0; i < w.length; i++) {
            z = 0;
            for (int j = 0; j < x.length; j++) {
//                System.out.println(x[j] +"  "+ w[i][j]);
                z += x[j] * w[i][j];
            }
            y[i] = sigmoid(z - o[i]);
            //调试当前节点输出
//            System.out.println(z + "   " + o[i] + "   " + y[i]);
        }



    }

    /**
     * 激励函数
     * @param x
     * @return
     */
    public double sigmoid(double x){
        return 1/(1+Math.exp(-x));
    }
    public double sigmoidX(double x){
        return 2*1.716/(1+Math.exp(-x*0.667))-1.716;
    }

    /**
     * 反向传播
     * @param out 当前实际输出
     */
    public void backword(double[] out){

        getOutError(out, outE);// 获取输出层的误差梯度outE；

        getHideError(outE, hideE);// 获取隐含层的误差梯度hideE；

        updateWeight(hideE, hideW, input, hideO);// 更新隐含层的权值hideW；阈值hideO

        updateWeight(outE, outW, outX, outO);// 更新输出层的权值outW；阈值outO

    }

    /**
     * 更新权重、阈值
     * @param error 误差梯度
     * @param weight 原本的权重
     * @param x 当前层输入
     * @param o 阈值
     */
    private void updateWeight(double[] error, double[][] weight, double[] x, double[] o) {
        //更新权重
        for (int i = 0; i < weight.length; i++) {
            for (int j = 0; j < weight[i].length; j++) {
                weight[i][j] = weight[i][j] + rate * error[i] * x[j];
            }
        }
        //更新阈值
        for (int i = 0; i < o.length; i++) {
            o[i] = o[i] - rate * error[i];
        }

    }
    /**
     * 获取输出层的误差梯度
     * @param output 预测输出值
     * @param outE 输出层的误差
     */
    public void getOutError(double[] output, double[] outE) {
        double e;
        for (int i = 0; i < exceptOut.length; i++) {
            e = exceptOut[i] - output[i];//误差
            outE[i] = output[i] * (1 - output[i]) * e;
            //System.out.println("【【【【"+exceptOut[i] +"】】】"+ output[i]);
        }

    }

    /**
     * 获取隐含层的误差梯度
     * @param NeLaErr 输出层的误差梯度
     * @param error 隐含误差梯度数组
     */
    public void getHideError(double[] NeLaErr, double[] error) {

        for (int j = 0; j < error.length; j++) {
            double sum = 0;
            for (int k = 0; k < outW.length; k++) {
                sum += NeLaErr[k] * outW[k][j];//求和所有输出层（误差梯度*权重）
            }
            error[j] = outX[j] * (1 - outX[j]) * sum;//当前隐含层误差梯度
        }
    }

    /**
     * 预测
     * @param data 预测数据
     * @param output 输出值
     */
    public void preResult(double[] data, double[] output) {

        double[] out_y = new double[outW.length];
        System.arraycopy(data, 0, input, 0, data.length);
        forword(input, out_y);
        System.arraycopy(out_y, 0, output, 0, out_y.length);

    }
}

