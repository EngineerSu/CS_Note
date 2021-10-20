package com.vivo.internet;

import static org.junit.Assert.assertTrue;

import com.vivo.internet.pojo.Person;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MapSolrParams;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    //设置solr客户端url地址
    private String solrUrl = "http://localhost:8983/solr/learncore";

    /**
     * 连接solr客户端
     */
    @Test
    public void testConectionClient() {
        //创建solrClient同时指定超时时间，不指定走默认配置
        HttpSolrClient solrClient = new HttpSolrClient.Builder(solrUrl)
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();
        System.out.println(solrClient);
    }

    /**
     * doc建立索引
     * @throws IOException
     * @throws SolrServerException
     */
    @Test
    public void addIndexById() throws IOException, SolrServerException {
        HttpSolrClient solrClient = new HttpSolrClient.Builder(solrUrl).build();
        //创建索引文档对象
        SolrInputDocument doc = new SolrInputDocument();
        // 第一个参数：域的名称，域的名称必须是在schema.xml中定义的
        // 第二个参数：域的值,注意：id的域不能少
        doc.addField("id","12");
        doc.addField("hobby","数钱");
        doc.addField( "name", "2豆");
//        doc.addField("price","1.2");
        //3.将文档写入索引库中
        solrClient.add(doc);
        solrClient.commit();
    }

    /**
     * docBean建立索引
     * @throws IOException
     * @throws SolrServerException
     */
    @Test
    public void addIndexByBean() throws IOException, SolrServerException {
        HttpSolrClient solrClient = new HttpSolrClient.Builder(solrUrl).build();
        //创建索引文档Bean对象
        Person person = new Person("苏畅", "骑车看电影");
        //3.将文档写入索引库中
        solrClient.addBean(person);
        solrClient.commit();
    }

    /**
     * 批量doc建立索引
     * @throws Exception
     */
    @Test
    public void addIndexByListId() throws Exception {
        HttpSolrClient solrClient = new HttpSolrClient.Builder(solrUrl).build();
        //创建索引文档对象
        SolrInputDocument doc1 = new SolrInputDocument();
        doc1.addField( "id", "12");
        doc1.addField( "name", "2豆");
//        doc1.addField( "price", 1.8 );
        SolrInputDocument doc2 = new SolrInputDocument();
        doc2.addField( "id", "13" );
        doc2.addField( "name", "3豆" );
//        doc2.addField( "price", 2.6 );
        Collection<SolrInputDocument> docs = new ArrayList<>();
//        ArrayList<SolrInputDocument> docs = new ArrayList<>();
        docs.add(doc1);
        docs.add(doc2);
        //3.将文档写入索引库中
        solrClient.add(docs);
        solrClient.commit();
    }

    /**
     * 批量docBean建立索引
     * @throws IOException
     * @throws SolrServerException
     */
    @Test
    public void addIndexByBeans() throws IOException, SolrServerException {
        HttpSolrClient solrClient = new HttpSolrClient.Builder(solrUrl).build();
        //创建索引文档Bean对象
        Person person1 = new Person("妹妥宝宝", "看电影打豆豆");
        Person person2 = new Person("鸿蒙", "打安卓做系统");
        Collection<Person> persons = new ArrayList<>();
        persons.add(person1);
        persons.add(person2);
        //3.将文档写入索引库中
        solrClient.addBeans(persons);
        solrClient.commit();
    }

    /**
     * 匹配查询
     * @throws IOException
     * @throws SolrServerException
     */
    @Test
    public void findIndex1() throws IOException, SolrServerException {
        int pageSize = 3; // 每页显示3条
        int page = 1; // 设置当前查询页码
        HttpSolrClient solrClient = new HttpSolrClient.Builder(solrUrl).build();
        // 创建搜索对象
        SolrQuery query = new SolrQuery();
        // 设置q搜索条件
        query.set("q","name:豆");
        // 设置fq搜索条件
//        query.set("fq","price:[1 TO 20]"); // [1 TO 20]等价于1<=price<=20 (1 TO 20)等价于1<price<20
        // 分页查询
        query.setStart((page - 1) * pageSize);
        query.setRows(pageSize);
        // 设置排序
        query.setSort("id", SolrQuery.ORDER.asc);
        // 设置过滤(相当于级联搜索条件?)
        query.setFilterQueries("name:2");
        //发起搜索请求
        QueryResponse response = solrClient.query(query);
        // 查询结果
        SolrDocumentList docs = response.getResults();
        // 查询结果总数
        long cnt = docs.getNumFound();
        System.out.println("总条数为"+cnt+"条");
        for (SolrDocument doc : docs) {
            System.out.println("id:"+ doc.get("id") + ",name:"+ doc.get("name") + ",hobby:"+ doc.get("hobby"));
        }
        solrClient.close();
    }

    /**
     * 条件过滤查询
     * @throws IOException
     * @throws SolrServerException
     */
    @Test
    public void findIndex2() throws IOException, SolrServerException {
        HttpSolrClient solrClient = new HttpSolrClient.Builder(solrUrl).build();
        //2 封装查询参数
        Map<String, String> queryParamMap = new HashMap<String, String>();
        queryParamMap.put("q", "name:豆");
        //3 添加到SolrParams对象,SolrParams 有一个 SolrQuery 子类，它提供了一些方法极大地简化了查询操作
        MapSolrParams queryParams = new MapSolrParams(queryParamMap);
        //4 执行查询返回QueryResponse
        QueryResponse response = solrClient.query(queryParams);
        //5 获取doc文档
        SolrDocumentList docs = response.getResults();
        // 查询结果总数
        long cnt = docs.getNumFound();
        System.out.println("总条数为" + cnt + "条");
        //[6]内容遍历
        for (SolrDocument doc : docs) {
            System.out.println("id:" + doc.get("id") + ",name:" + doc.get("name") + ",price:" + doc.get("price"));
        }
        solrClient.close();
    }

    /**
     * 高亮查询 还未成功
     * @throws SolrServerException
     */
    @Test
    public void findWithHighLighting() throws SolrServerException, IOException {
        HttpSolrClient solrClient = new HttpSolrClient.Builder(solrUrl).build();
        SolrQuery query = new SolrQuery();
        query.setQuery("name:豆");
        // 开启高亮组件
        query.setHighlight(true);
        // 高亮字段
        query.addHighlightField("name");
        // 前缀标记
        query.setHighlightSimplePre("<span color='red'>");
        // 后缀标记
        query.setHighlightSimplePost("</span>");
        // 查询
        QueryResponse response = solrClient.query(query);
        // 获取结果
        SolrDocumentList docs = response.getResults();
        System.out.println("查询文档总数: " + docs.getNumFound());
        for (SolrDocument doc : docs) {
            System.out.println("id:" + doc.get("id") + ",name:" + doc.get("name") + ",hobby:" + doc.get("hobby"));
            // 高亮信息
            if (response.getHighlighting() != null) {
                if (response.getHighlighting().get("id") != null) {
                    Map<String, List<String>> map = response.getHighlighting().get("id");// 取出高亮片段
                    if (map.get("name") != null) {
                        for (String s : map.get("name")) {
                            System.out.println(s);
                            System.out.println("到这里啦!");
                        }
                    }
                }
            }
        }
    }

    /**
     * 单一条件删除
     * @throws IOException
     * @throws SolrServerException
     */
    @Test
    public void deleteIndexById() throws IOException, SolrServerException {
        HttpSolrClient solrClient = new HttpSolrClient.Builder(solrUrl).build();
        //全删
        //solrClient.deleteByQuery("*:*");
        //模糊匹配删除（带有分词效果的删除）
        solrClient.deleteByQuery("hobby:swim");
        //指定id删除
//        solrClient.deleteById("id");
        solrClient.commit();
    }

    /**
     * 按索引批量删除
     * @throws IOException
     * @throws SolrServerException
     */
    @Test
    public void deleteIndexByListId() throws IOException, SolrServerException {
        HttpSolrClient solrClient = new HttpSolrClient.Builder(solrUrl).build();
        //通过id删除
        ArrayList<String> ids = new ArrayList<>();
        ids.add("1");
        ids.add("3");
        solrClient.deleteById(ids);
        //[3]提交
        solrClient.commit();
        //[4]关闭资源
        solrClient.close();
    }
}
