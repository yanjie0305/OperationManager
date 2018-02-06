package com.example.edaibu.operationmanager.bean;

import java.util.List;

/**
 * Created by ${gyj} on 2018/1/2.
 */

public class test {

    /**
     * code : 200
     * msg : null
     * data : [{"id":"5922922faa2addf62231309d","name":"B片区","coordinates":[[116.576976,40.045071],[116.560878,39.987161],[116.623544,39.997774],[116.618945,40.032919],[116.576976,40.045071]]},{"id":"597186c5e6957cda5fbd5ee7","name":"天津广博片区","coordinates":[[115.482314,39.949824],[115.703081,39.975484],[115.796218,39.831131],[115.732977,39.753958],[115.708831,39.733541],[115.581199,39.759283],[115.496112,39.781466],[115.466216,39.934778],[115.482314,39.949824]]},{"id":"599ec99bee5c397e98702b64","name":"北京片区","coordinates":[[116.246377,40.079573],[116.281734,40.097237],[116.303006,40.103198],[116.313642,40.090835],[116.328015,40.094919],[116.331392,40.093622],[116.358377,40.096189],[116.421151,40.097016],[116.422013,40.087964],[116.488129,40.088185],[116.491866,40.019924],[116.466857,40.020698],[116.416264,40.027438],[116.366965,40.027217],[116.390105,39.993507],[116.307461,39.990301],[116.305449,39.996036],[116.285938,39.996754],[116.288453,40.003083],[116.289567,40.011428],[116.28892,40.020159],[116.279362,40.033129],[116.261899,40.046552],[116.246377,40.079573]]},{"id":"59cde56239bb575b29b34593","name":"天久片区","coordinates":[[116.400448,40.062841],[116.413671,40.064608],[116.411947,40.086471],[116.488985,40.088016],[116.489848,40.026164],[116.402173,40.026827],[116.400448,40.062841]]}]
     */

    private int code;
    private Object msg;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 5922922faa2addf62231309d
         * name : B片区
         * coordinates : [[116.576976,40.045071],[116.560878,39.987161],[116.623544,39.997774],[116.618945,40.032919],[116.576976,40.045071]]
         */

        private String id;
        private String name;
        private List<List<Double>> coordinates;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<List<Double>> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(List<List<Double>> coordinates) {
            this.coordinates = coordinates;
        }
    }
}
