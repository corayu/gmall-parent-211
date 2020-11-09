package com.atguigu.gmall.test.juc9;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.SkuInfo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;


public class Test {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // a方法，用自由变量
        // a();
        // b方法，级联写法，不用自由变量
        // b();

        // 查询商品详情的线程
      /*  CompletableFuture<SkuInfo> completableFutureSkuInfo = CompletableFuture.supplyAsync(new Supplier<SkuInfo>(){
                      @Override
                      public SkuInfo get() {
                          SkuInfo skuInfo = new SkuInfo();
                          System.out.println("查询skuInfo的线程执行");
                          skuInfo.setSkuName("联想拯救者y7000");
                          skuInfo.setCategory3Id(61l);
                          return skuInfo;
                      }
                  }
        );*/
        CompletableFuture<SkuInfo> completableFutureSkuInfo = CompletableFuture.supplyAsync(new Supplier<SkuInfo>() {
            @Override
            public SkuInfo get() {
                SkuInfo skuInfo = new SkuInfo();
                System.out.println("查询skuInfo的线程执行");
                skuInfo.setSkuName("联想拯救者y7000");
                skuInfo.setCategory3Id(61l);
                return skuInfo;
            }
        });

        // 查询商品价格的线程
      /*  CompletableFuture<Double> completableFuturePrice = CompletableFuture.supplyAsync(new Supplier<Double>(){
                         @Override
                         public Double get() {
                             System.out.println("查询商品价格的线程执行");
                             return 100d;
                         }
                     }
        );*/
        CompletableFuture<Double> completableFuturePrice = CompletableFuture.supplyAsync(new Supplier<Double>() {
            @Override
            public Double get() {
                System.out.println("查询商品价格的线程执行");
                return 10d;
            }
        });

        // 查询商品分类的线程
        /*CompletableFuture<BaseCategory1> completableFutureBaseCategory1 = completableFutureSkuInfo.thenApplyAsync(new Function<SkuInfo, BaseCategory1>() {
            @Override
            public BaseCategory1 apply(SkuInfo skuInfo) {

                BaseCategory1 baseCategory1 = new BaseCategory1();
                baseCategory1.setId(61l);
                return baseCategory1;
            }
        });*/
        CompletableFuture<BaseCategory1> completableFutureBaseCategory1 = completableFutureSkuInfo.thenApplyAsync(new Function<SkuInfo, BaseCategory1>() {
            @Override
            public BaseCategory1 apply(SkuInfo skuInfo) {
                BaseCategory1 baseCategory1 = new BaseCategory1();
                baseCategory1.setId(61l);
                return baseCategory1;
            }
        });

        //CompletableFuture.allOf(completableFutureSkuInfo,completableFuturePrice,completableFutureBaseCategory1).join();

        CompletableFuture.anyOf(completableFutureSkuInfo).join();
        SkuInfo skuInfo = completableFutureSkuInfo.get();
        Double aDouble = completableFuturePrice.get();
        BaseCategory1 baseCategory1 = completableFutureBaseCategory1.get();


        System.out.println(skuInfo.getSkuName());
        System.out.println(aDouble);
        System.out.println(baseCategory1.getId());


    }

    private static void b() throws ExecutionException, InterruptedException {
        CompletableFuture<Long> completableFuture = CompletableFuture.supplyAsync(new Supplier<Long>() {
                                                                                      @Override
                                                                                      public Long get() {
                                                                                          System.out.println("completableFuture线程开始执行");
                                                                                          int i = 1/0;
                                                                                          return 1024l;
                                                                                      }
                                                                                  }
        ).exceptionally(new Function<Throwable, Long>() {
            @Override
            public Long apply(Throwable throwable) {
                System.out.println("出异常了");
                return 0l;
            }
        }).whenComplete(new BiConsumer<Long, Throwable>() {
            @Override
            public void accept(Long aLong, Throwable throwable) {
                System.out.println("whenComplete"+aLong);
            }
        });

        Long aLong = completableFuture.get();
        System.out.println("主线程："+aLong);
    }

    private static void a() throws InterruptedException, ExecutionException {
        CompletableFuture<Long> completableFuture = CompletableFuture.supplyAsync(new Supplier<Long>() {
                                                                                      @Override
                                                                                      public Long get() {
                                                                                          System.out.println("completableFuture线程开始执行");
                                                                                          int i = 1/0;
                                                                                          return 1024l;
                                                                                      }
                                                                                  }
        );

        completableFuture.exceptionally(new Function<Throwable, Long>() {
            @Override
            public Long apply(Throwable throwable) {
                System.out.println("出异常了");
                return 0l;
            }
        });

        completableFuture.whenComplete(new BiConsumer<Long, Throwable>() {
            @Override
            public void accept(Long aLong, Throwable throwable) {
                System.out.println("whenComplete"+aLong);
            }
        });

        Long aLong = completableFuture.get();
        System.out.println("主线程："+aLong);
    }
}
