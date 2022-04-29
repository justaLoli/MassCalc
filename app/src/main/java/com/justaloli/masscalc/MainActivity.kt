package com.justaloli.masscalc

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var mm = mutableMapOf<String,Double>()//保存原子质量的map
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)//家在layout
        window.navigationBarColor = Color.TRANSPARENT //设置导航栏沉浸
        //状态栏和自动夜间模式沉浸写在styles.xml里

        initMassMap()//加载原子质量map

        //输入框的相关绑定
        editText.setOnEditorActionListener { view, i, event ->
            if(i==EditorInfo.IME_ACTION_SEARCH){ //将输入法回车事件绑定给处理函数
                processInput()
            }
            true //这是个什么参数，不知道
        }

        //配置输入时的候选框
        val adapter = ArrayAdapter<String>(this,
            R.layout.support_simple_spinner_dropdown_item,
            mm.keys.toList()) //定义候选框 adapter
        editText.setAdapter(adapter) //设置输入框的候选为adapter（上一行的）
        editText.setOnItemClickListener { _, _, _, _ ->  //这四个参数目前用不到
            processInput()}//当点击候选框中item的时候 直接进行processInput()方法，无需再次回车

        //按钮的相关绑定
        button.setOnClickListener {
            myprint(mm.toList().toString())
        }//设置点按打印全部原子质量
        button.setOnLongClickListener{
            textView.text = "";true //这个true是这个长按监听需要的一个值，没啥用，就true吧
        }//设置长按清屏

        //设置scrollview自动滚到底
        scrollView.viewTreeObserver.addOnGlobalLayoutListener {
            scrollView.post(){
                kotlin.run { scrollView.fullScroll(View.FOCUS_DOWN)}
            }
        }
    }
    private fun processInput(){ //处理
        val input = editText.text.toString() //获取输入框的输入
        val mass = calcMain(input) //调用计算函数
        if(mass!=0.0) {
            val massStr = String.format("%.3f", mass)
            myprint("$input : $massStr") //输出结果
            ShareUtil.putString(input,mass.toString(),applicationContext)
        }
    }

    // 两个用于相对分子质量的函数
    private fun calcMass(li:Array<String>):Double{ //主要用于匹配每个原子和对应的系数，进行相乘再相加的运算
        var r=0.0
        val re = "[A-Z][a-z]*|[0-9]*"
        for (i in li){
            var relist = emptyArray<String>()
            Regex(re).findAll(i).forEach {relist+=it.value}
            if(relist[1]==""){
                relist[1] = "1"
            }
            val m = mm.getOrDefault(relist[0],0.0)
            if(m==0.0){return 0.0}
            r+=m*(relist[1].toInt())
        }
        return r
    }
    private fun calcMain(input:String):Double{ //主要用于识别、拆分输入中的括号，并累加全部计算结果
        mm[input]?.also { return it }
        var res = 0.0
        var allMatch = ""
        Regex("\\([^()]*(((\\()[^()]*)+((\\))[^()]*)+)*\\)[0-9]*|[A-Z][a-z]?[0-9]*").findAll(input).forEach {
            val i = it.value
            allMatch+=i
            if(i!=""){
                val r: Double
//                val t: Int
                if(i[0]=='('){
                    var inp_li = emptyArray<String>()
                    Regex(".*\\)|[0-9]*").findAll(i).forEach { inp_li+=it.value }
                    if(inp_li[1]==""){inp_li[1] = "1"}
//                    myprint(inp_li.toList().toString())
//                    myprint(inp_li[0].substring(1,inp_li[0].lastIndex))
                    r = calcMain(inp_li[0].substring(1,inp_li[0].lastIndex))*inp_li[1].toInt()
                }
                else{
                    r = calcMass(arrayOf(i))
                }
                if(r==0.0){
                    myprint("input incorrect. input: $input")
                    return 0.0
                }
                res+=r
            }
        }
//        if(allMatch!=input){
        if(allMatch!=input){
            myprint("input incorrect. input: $input")
            res = 0.0
        }
        //写入历史部分 目前停用
//        if(isOutLayer){
//            if(res!=0.0 && switchHistory.isChecked) {
//                    openFileOutput(input, MODE_PRIVATE).use {
//                        it.write(res.toString().toByteArray())
//                    }
//            }
//        }
        return  res
    }
    //初始化原子质量map
    private fun initMassMap(){
        mm.putAll(listOf(Pair("H",1.008),Pair("He",4.0026),Pair("Li",6.94),Pair("Be",9.0122),Pair("B",10.81),Pair("C",12.011),Pair("N",14.007),Pair("O",15.999),Pair("F",18.998),Pair("Ne",20.18),Pair("Na",22.99),Pair("Mg",24.305),Pair("Al",26.982),Pair("Si",28.085),Pair("P",30.974),Pair("S",32.06),Pair("Cl",35.45),Pair("Ar",39.948),Pair("K",39.098),Pair("Ca",40.078),Pair("Sc",44.956),Pair("Ti",47.867),Pair("V",50.942),Pair("Cr",51.996),Pair("Mn",54.938),Pair("Fe",55.845),Pair("Co",58.933),Pair("Ni",58.693),Pair("Cu",63.546),Pair("Zn",65.38),Pair("Ga",69.723),Pair("Ge",72.63),Pair("As",74.922),Pair("Se",78.971),Pair("Br",79.904),Pair("Kr",83.798),Pair("Rb",85.468),Pair("Sr",87.62),Pair("Y",88.906),Pair("Zr",91.224),Pair("Nb",92.906),Pair("Mo",95.95),Pair("Tc",98.0),Pair("Ru",101.07),Pair("Rh",102.91),Pair("Pd",106.42),Pair("Ag",107.87),Pair("Cd",112.41),Pair("In",114.82),Pair("Sn",204.38),Pair("Sb",121.76),Pair("Te",127.6),Pair("I",126.9),Pair("Xe",131.29),Pair("Cs",132.91),Pair("Ba",137.33),Pair("La",138.91),Pair("Ce",140.12),Pair("Pr",140.91),Pair("Nd",144.24),Pair("Pm",144.24),Pair("Sm",150.36),Pair("Eu",151.96),Pair("Gd",157.25),Pair("Tb",158.93),Pair("Dy",162.5),Pair("Ho",164.93),Pair("Er",167.26),Pair("Tm",168.93),Pair("Yb",173.05),Pair("Lu",174.97),Pair("Hf",178.49),Pair("Ta",180.95),Pair("W",183.84),Pair("Re",186.21),Pair("Os",190.23),Pair("Ir",192.22),Pair("Pt",195.08),Pair("Au",196.97),Pair("Hg",200.59),Pair("Tl",204.38),Pair("Pb",207.2),Pair("Bi",208.98),Pair("Po",209.0),Pair("At",210.0),Pair("Rn",222.0),Pair("Fr",223.0),Pair("Ra",226.0),Pair("Ac",227.0),Pair("Th",232.04),Pair("Pa",231.04),Pair("U",238.03),Pair("Np",237.0),Pair("Pu",244.0),Pair("Am",243.0),Pair("Cm",247.0),Pair("Bk",247.0),Pair("Cf",251.0),Pair("Es",252.0),Pair("Fm",257.0),Pair("Md",258.0),Pair("No",259.0),Pair("Lr",266.0),Pair("Rf",267.0),Pair("Db",268.0),Pair("Sg",269.0),Pair("Bh",270.0),Pair("Hs",277.0),Pair("Mt",278.0),Pair("Ds",281.0),Pair("Rg",282.0),Pair("Cn",282.0),Pair("Nh",286.0),Pair("Fl",289.0),Pair("Mc",290.0),Pair("Lv",293.0),Pair("Ts",294.0),Pair("Og",294.0),))
        //把应用数据里面的历史记录存起来 也放到mm字典里面 目前停用
//        var historyList = arrayOf<Pair<String,Double>>()
//        for(fn in fileList()){
//            val mass = openFileInput(fn).bufferedReader().readText().toDouble()
//            historyList+=Pair(fn,mass)
//        }
//        mm.putAll(historyList)
    }
    object ShareUtil{
        var sps: SharedPreferences?=null
        fun getSps(context: Context): SharedPreferences {
            if(sps==null){
                sps = context.getSharedPreferences("default", Context.MODE_PRIVATE)
            }
            return sps!!
        }
        fun putString(key:String,value:String?,context: Context){
            if(!value.isNullOrBlank()){
                val editor: SharedPreferences.Editor = getSps(context).edit()
                editor.putString(key,value)
                editor.apply()
            }
        }
        fun getString(key:String,context: Context):String?{
            if(!key.isBlank()){
                val sps = getSps(context)
                return sps.getString(key,null)
            }
            return null
        }

    }
    //打印输出结果函数
    private fun myprint(text:String, isclear:Boolean=false){
        if(isclear){textView.text=""}
        textView.append("\n$text\n")

    }
}