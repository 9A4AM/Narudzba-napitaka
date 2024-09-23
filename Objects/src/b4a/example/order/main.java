package b4a.example.order;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;
    public static boolean dontPause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.example.order", "b4a.example.order.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "b4a.example.order", "b4a.example.order.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.example.order.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create " + (isFirst ? "(first time)" : "") + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        if (!dontPause)
            BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (main) Pause event (activity is not paused). **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        if (!dontPause) {
            processBA.setActivityPaused(true);
            mostCurrent = null;
        }

        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            main mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.collections.List _drinkslist = null;
public static anywheresoftware.b4a.objects.collections.Map _ordersmap = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinner1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnadddrink = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnshoworders = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btndeletedrink = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnclearall = null;
public anywheresoftware.b4a.objects.ListViewWrapper _listview1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnreducedrink = null;
public anywheresoftware.b4a.phone.Phone.PhoneWakeState _pho = null;
public b4a.example.order.starter _starter = null;
public b4a.example.order.b4xpages _b4xpages = null;
public b4a.example.order.b4xcollections _b4xcollections = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 30;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 31;BA.debugLine="Activity.LoadLayout(\"Main\") ' Učitava glavni layo";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 34;BA.debugLine="pho.KeepAlive(True)";
mostCurrent._pho.KeepAlive(processBA,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 37;BA.debugLine="drinksList.Initialize";
_drinkslist.Initialize();
 //BA.debugLineNum = 38;BA.debugLine="drinksList.AddAll(Array As String(\"Kava\", \"Kava s";
_drinkslist.AddAll(anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{"Kava","Kava s mlijekom","Kapučino","Čaj","Piva","Radler","Sommersby","Vino","Voda","Mineralna voda","Sok negazirani","Sok gazirani","Gemišt","Bambus","Jeger","Pelinkovac","Višnjevac","Orahovac","Čokoladni liker","Rakija","Liker","Gin Tonic","Kruškovac","Ostalo..."}));
 //BA.debugLineNum = 40;BA.debugLine="Spinner1.AddAll(drinksList) ' Dodavanje napitaka";
mostCurrent._spinner1.AddAll(_drinkslist);
 //BA.debugLineNum = 41;BA.debugLine="ordersMap.Initialize ' Inicijalizacija mape narud";
_ordersmap.Initialize();
 //BA.debugLineNum = 43;BA.debugLine="LoadOrders ' Učitavanje prethodnih narudžbi iz da";
_loadorders();
 //BA.debugLineNum = 46;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 53;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 55;BA.debugLine="pho.ReleaseKeepAlive";
mostCurrent._pho.ReleaseKeepAlive();
 //BA.debugLineNum = 56;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 48;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 50;BA.debugLine="pho.KeepAlive(True)";
mostCurrent._pho.KeepAlive(processBA,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 51;BA.debugLine="End Sub";
return "";
}
public static String  _btnadddrink_click() throws Exception{
String _selecteddrink = "";
anywheresoftware.b4a.agraham.dialogs.InputDialog _dlg = null;
int _res = 0;
 //BA.debugLineNum = 58;BA.debugLine="Sub btnAddDrink_Click";
 //BA.debugLineNum = 60;BA.debugLine="Dim selectedDrink As String = Spinner1.SelectedIt";
_selecteddrink = mostCurrent._spinner1.getSelectedItem();
 //BA.debugLineNum = 62;BA.debugLine="If selectedDrink = \"Ostalo...\" Then";
if ((_selecteddrink).equals("Ostalo...")) { 
 //BA.debugLineNum = 64;BA.debugLine="Dim dlg As InputDialog";
_dlg = new anywheresoftware.b4a.agraham.dialogs.InputDialog();
 //BA.debugLineNum = 65;BA.debugLine="Dim res As Int = dlg.Show(\"Unesite naziv napitka";
_res = _dlg.Show("Unesite naziv napitka:","Novi Napitak","OK","Odustani","",mostCurrent.activityBA,(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 66;BA.debugLine="If res = DialogResponse.POSITIVE Then";
if (_res==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 67;BA.debugLine="selectedDrink = dlg.Input";
_selecteddrink = _dlg.getInput();
 }else {
 //BA.debugLineNum = 69;BA.debugLine="Return ' Ako je korisnik odustao, prekinuti rad";
if (true) return "";
 };
 };
 //BA.debugLineNum = 74;BA.debugLine="If ordersMap.ContainsKey(selectedDrink) Then";
if (_ordersmap.ContainsKey((Object)(_selecteddrink))) { 
 //BA.debugLineNum = 75;BA.debugLine="ordersMap.Put(selectedDrink, ordersMap.Get(selec";
_ordersmap.Put((Object)(_selecteddrink),(Object)((double)(BA.ObjectToNumber(_ordersmap.Get((Object)(_selecteddrink))))+1));
 }else {
 //BA.debugLineNum = 77;BA.debugLine="ordersMap.Put(selectedDrink, 1)";
_ordersmap.Put((Object)(_selecteddrink),(Object)(1));
 };
 //BA.debugLineNum = 80;BA.debugLine="SaveOrders ' Spremanje narudžbi u datoteku";
_saveorders();
 //BA.debugLineNum = 81;BA.debugLine="btnShowOrders_Click ' Osvježi prikaz narudžbi nak";
_btnshoworders_click();
 //BA.debugLineNum = 82;BA.debugLine="End Sub";
return "";
}
public static String  _btnclearall_click() throws Exception{
 //BA.debugLineNum = 119;BA.debugLine="Sub btnClearAll_Click";
 //BA.debugLineNum = 121;BA.debugLine="If Msgbox2(\"Jeste li sigurni da želite obrisati s";
if (anywheresoftware.b4a.keywords.Common.Msgbox2(BA.ObjectToCharSequence("Jeste li sigurni da želite obrisati sve narudžbe?"),BA.ObjectToCharSequence("Potvrda"),"Da","Ne","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA)==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 122;BA.debugLine="ordersMap.Clear";
_ordersmap.Clear();
 //BA.debugLineNum = 123;BA.debugLine="SaveOrders";
_saveorders();
 //BA.debugLineNum = 124;BA.debugLine="btnShowOrders_Click ' Osvježi prikaz nakon brisa";
_btnshoworders_click();
 };
 //BA.debugLineNum = 126;BA.debugLine="End Sub";
return "";
}
public static String  _btndeletedrink_click() throws Exception{
String _selecteddrink = "";
anywheresoftware.b4a.agraham.dialogs.InputDialog _dlg = null;
int _res = 0;
 //BA.debugLineNum = 92;BA.debugLine="Sub btnDeleteDrink_Click";
 //BA.debugLineNum = 93;BA.debugLine="Dim selectedDrink As String = Spinner1.SelectedIt";
_selecteddrink = mostCurrent._spinner1.getSelectedItem();
 //BA.debugLineNum = 94;BA.debugLine="If selectedDrink = \"Ostalo...\" Then";
if ((_selecteddrink).equals("Ostalo...")) { 
 //BA.debugLineNum = 95;BA.debugLine="Dim dlg As InputDialog";
_dlg = new anywheresoftware.b4a.agraham.dialogs.InputDialog();
 //BA.debugLineNum = 96;BA.debugLine="Dim res As Int = dlg.Show(\"Unesite naziv napitka";
_res = _dlg.Show("Unesite naziv napitka za brisanje:","Brisanje Napitka","OK","Odustani","",mostCurrent.activityBA,(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 97;BA.debugLine="If res = DialogResponse.POSITIVE Then";
if (_res==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 98;BA.debugLine="selectedDrink = dlg.Input";
_selecteddrink = _dlg.getInput();
 }else {
 //BA.debugLineNum = 100;BA.debugLine="Return ' Ako je korisnik odustao, prekinuti rad";
if (true) return "";
 };
 };
 //BA.debugLineNum = 104;BA.debugLine="If selectedDrink <> \"\" Then";
if ((_selecteddrink).equals("") == false) { 
 //BA.debugLineNum = 105;BA.debugLine="If ordersMap.ContainsKey(selectedDrink) Then";
if (_ordersmap.ContainsKey((Object)(_selecteddrink))) { 
 //BA.debugLineNum = 106;BA.debugLine="If Msgbox2(\"Jeste li sigurni da želite obrisati";
if (anywheresoftware.b4a.keywords.Common.Msgbox2(BA.ObjectToCharSequence("Jeste li sigurni da želite obrisati "+_selecteddrink+"?"),BA.ObjectToCharSequence("Potvrda"),"Da","Ne","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA)==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 107;BA.debugLine="ordersMap.Remove(selectedDrink)";
_ordersmap.Remove((Object)(_selecteddrink));
 //BA.debugLineNum = 108;BA.debugLine="SaveOrders ' Ponovno spremanje narudžbi nakon";
_saveorders();
 //BA.debugLineNum = 109;BA.debugLine="btnShowOrders_Click ' Osvježi prikaz nakon bri";
_btnshoworders_click();
 };
 }else {
 //BA.debugLineNum = 112;BA.debugLine="ToastMessageShow(\"Napitak ne postoji u narudžbi";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Napitak ne postoji u narudžbi."),anywheresoftware.b4a.keywords.Common.False);
 };
 }else {
 //BA.debugLineNum = 115;BA.debugLine="ToastMessageShow(\"Nema odabranog napitka.\", Fals";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Nema odabranog napitka."),anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 117;BA.debugLine="End Sub";
return "";
}
public static String  _btnreducedrink_click() throws Exception{
String _selecteddrink = "";
anywheresoftware.b4a.agraham.dialogs.InputDialog _dlg = null;
int _res = 0;
int _currentamount = 0;
 //BA.debugLineNum = 128;BA.debugLine="Sub btnReduceDrink_Click";
 //BA.debugLineNum = 129;BA.debugLine="Dim selectedDrink As String = Spinner1.SelectedIt";
_selecteddrink = mostCurrent._spinner1.getSelectedItem();
 //BA.debugLineNum = 130;BA.debugLine="If selectedDrink = \"Ostalo...\" Then";
if ((_selecteddrink).equals("Ostalo...")) { 
 //BA.debugLineNum = 131;BA.debugLine="Dim dlg As InputDialog";
_dlg = new anywheresoftware.b4a.agraham.dialogs.InputDialog();
 //BA.debugLineNum = 132;BA.debugLine="Dim res As Int = dlg.Show(\"Unesite naziv napitka";
_res = _dlg.Show("Unesite naziv napitka za smanjenje količine:","Smanjenje Napitka","OK","Odustani","",mostCurrent.activityBA,(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 133;BA.debugLine="If res = DialogResponse.POSITIVE Then";
if (_res==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 134;BA.debugLine="selectedDrink = dlg.Input";
_selecteddrink = _dlg.getInput();
 }else {
 //BA.debugLineNum = 136;BA.debugLine="Return ' Ako je korisnik odustao, prekinuti rad";
if (true) return "";
 };
 };
 //BA.debugLineNum = 140;BA.debugLine="If selectedDrink <> \"\" Then";
if ((_selecteddrink).equals("") == false) { 
 //BA.debugLineNum = 141;BA.debugLine="If ordersMap.ContainsKey(selectedDrink) Then";
if (_ordersmap.ContainsKey((Object)(_selecteddrink))) { 
 //BA.debugLineNum = 142;BA.debugLine="Dim currentAmount As Int = ordersMap.Get(select";
_currentamount = (int)(BA.ObjectToNumber(_ordersmap.Get((Object)(_selecteddrink))));
 //BA.debugLineNum = 143;BA.debugLine="If currentAmount > 1 Then";
if (_currentamount>1) { 
 //BA.debugLineNum = 144;BA.debugLine="ordersMap.Put(selectedDrink, currentAmount - 1";
_ordersmap.Put((Object)(_selecteddrink),(Object)(_currentamount-1));
 }else {
 //BA.debugLineNum = 146;BA.debugLine="ordersMap.Remove(selectedDrink) ' Ako je količ";
_ordersmap.Remove((Object)(_selecteddrink));
 };
 //BA.debugLineNum = 148;BA.debugLine="SaveOrders ' Spremi promjene";
_saveorders();
 //BA.debugLineNum = 149;BA.debugLine="btnShowOrders_Click ' Osvježi prikaz narudžbi n";
_btnshoworders_click();
 }else {
 //BA.debugLineNum = 151;BA.debugLine="ToastMessageShow(\"Napitak ne postoji u narudžbi";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Napitak ne postoji u narudžbi."),anywheresoftware.b4a.keywords.Common.False);
 };
 }else {
 //BA.debugLineNum = 154;BA.debugLine="ToastMessageShow(\"Nema odabranog napitka.\", Fals";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Nema odabranog napitka."),anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 156;BA.debugLine="End Sub";
return "";
}
public static String  _btnshoworders_click() throws Exception{
String _k = "";
 //BA.debugLineNum = 84;BA.debugLine="Sub btnShowOrders_Click";
 //BA.debugLineNum = 86;BA.debugLine="ListView1.Clear";
mostCurrent._listview1.Clear();
 //BA.debugLineNum = 87;BA.debugLine="For Each k As String In ordersMap.Keys";
{
final anywheresoftware.b4a.BA.IterableList group2 = _ordersmap.Keys();
final int groupLen2 = group2.getSize()
;int index2 = 0;
;
for (; index2 < groupLen2;index2++){
_k = BA.ObjectToString(group2.Get(index2));
 //BA.debugLineNum = 88;BA.debugLine="ListView1.AddSingleLine(k & \": \" & NumberFormat(";
mostCurrent._listview1.AddSingleLine(BA.ObjectToCharSequence(_k+": "+anywheresoftware.b4a.keywords.Common.NumberFormat((double)(BA.ObjectToNumber(_ordersmap.Get((Object)(_k)))),(int) (0),(int) (0))));
 }
};
 //BA.debugLineNum = 90;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 18;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 20;BA.debugLine="Dim Spinner1 As Spinner ' Spinner za dodavanje na";
mostCurrent._spinner1 = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Dim btnAddDrink As Button";
mostCurrent._btnadddrink = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Dim btnShowOrders As Button";
mostCurrent._btnshoworders = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim btnDeleteDrink As Button";
mostCurrent._btndeletedrink = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim btnClearAll As Button";
mostCurrent._btnclearall = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim ListView1 As ListView";
mostCurrent._listview1 = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim btnReduceDrink As Button ' Novi gumb za smanj";
mostCurrent._btnreducedrink = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim pho As PhoneWakeState ' Varijabla za sprječav";
mostCurrent._pho = new anywheresoftware.b4a.phone.Phone.PhoneWakeState();
 //BA.debugLineNum = 28;BA.debugLine="End Sub";
return "";
}
public static String  _loadorders() throws Exception{
 //BA.debugLineNum = 163;BA.debugLine="Sub LoadOrders";
 //BA.debugLineNum = 165;BA.debugLine="If File.Exists(File.DirInternal, \"orders.txt\") Th";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"orders.txt")) { 
 //BA.debugLineNum = 166;BA.debugLine="ordersMap = File.ReadMap(File.DirInternal, \"orde";
_ordersmap = anywheresoftware.b4a.keywords.Common.File.ReadMap(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"orders.txt");
 };
 //BA.debugLineNum = 168;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
starter._process_globals();
b4xpages._process_globals();
b4xcollections._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 14;BA.debugLine="Dim drinksList As List";
_drinkslist = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 15;BA.debugLine="Dim ordersMap As Map";
_ordersmap = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 16;BA.debugLine="End Sub";
return "";
}
public static String  _saveorders() throws Exception{
 //BA.debugLineNum = 158;BA.debugLine="Sub SaveOrders";
 //BA.debugLineNum = 160;BA.debugLine="File.WriteMap(File.DirInternal, \"orders.txt\", ord";
anywheresoftware.b4a.keywords.Common.File.WriteMap(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"orders.txt",_ordersmap);
 //BA.debugLineNum = 161;BA.debugLine="End Sub";
return "";
}
}
