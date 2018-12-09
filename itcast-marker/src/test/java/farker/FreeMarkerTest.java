package farker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.Test;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;


/*生成文件*/
public class FreeMarkerTest {

    @Test
    public void test() throws Exception{
        //	第一步： 创建一个 Configuration 对象;  构造方法的参数就是 freemarker 的版本号
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
        //	第二步：设置模板文件所在的路径
        configuration.setClassForTemplateLoading(FreeMarkerTest.class,"/ftl");
        //	第三步：设置模板文件使用的字符集；一般为 utf-8
        configuration.setDefaultEncoding("utf-8");
        //	第四步：获取模板
        Template template = configuration.getTemplate("test.ftl");
        //	第五步： 创建一个模板使用的数据集用来添加数据， 可以是 pojo 也可以是 map； 一般是 Map
        Map<String,Object> dataModel=new HashMap<>();
        dataModel.put("name","张宏浩");
        dataModel.put("message","真有钱");
        //	第六步：创建一个 Writer 对象，一般创建 FileWriter 对象，指定生成的文件名
        FileWriter fileWriter=new FileWriter("D:\\test.html");
        //	第七步：调用第四步的模板对象的 process 方法将第五步的数据输出给第六步的文件
        template.process(dataModel,fileWriter);
        //	第八步：关闭流
        fileWriter.close();
    }
}
