//package com.baomidou.plugin.idea.mybatisx.action;
//
//import com.intellij.database.actions.DatabaseObjectRefactoring;
//import com.intellij.database.model.DasObject;
//import com.intellij.database.model.DasTable;
//import com.intellij.database.model.DatabaseSystem;
//import com.intellij.database.psi.DbDataSource;
//import com.intellij.database.psi.DbDataSourceImpl;
//import com.intellij.database.psi.DbElement;
//import com.intellij.database.psi.DbPsiFacade;
//import com.intellij.database.util.DasUtil;
//import com.intellij.database.util.DbSqlUtil;
//import com.intellij.database.util.DbUtil;
//import com.intellij.database.view.DatabaseView;
//import com.intellij.database.view.ui.AbstractDbRefactoringDialog;
//import com.intellij.database.view.ui.DbTableDialog;
//import com.intellij.ide.actions.ActivateToolWindowAction;
//import com.intellij.openapi.actionSystem.*;
//import com.intellij.openapi.wm.ToolWindowManager;
//import com.intellij.psi.PsiElement;
//import com.intellij.util.Consumer;
//
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.Iterator;
//
///**
// * 一个线上的测试动作，主要用于测试一些骚操作。
// *
// * @author makejava
// * @version 1.0.0
// * @since 2018/08/03 08:55
// */
//public class TestAction extends AnAction {
//
//    @Override
//    public void actionPerformed(AnActionEvent e) {
//        ActionManager actionManager = ActionManager.getInstance();
//
//        // 获取到修改表动作（实现类com.intellij.database.actions.DatabaseObjectRefactoring）
//        AnAction anAction = actionManager.getAction("ModifyObject");
//
//        // 拿到上下文对象(上下文实现类com.intellij.ide.impl.DataManagerImpl$MyDataContext)
//        DataContext dataContext = e.getDataContext();
//
//        PsiElement psiElement = dataContext.getData(LangDataKeys.PSI_ELEMENT);
//
//        DbPsiFacade dbPsiFacade = DbPsiFacade.getInstance(e.getProject());
//
//        // 获取本地第一个数据源
//        DbDataSource localDataSource = dbPsiFacade.getDataSources().get(0);
//
//        DatabaseSystem databaseSystem = localDataSource.getDelegate();
//
//        // 获取一张表
//        DasTable table = DasUtil.getTables(localDataSource).get(0);
//
//        // 找到表的PSI对象
//        PsiElement psiElement2 = ((DbDataSourceImpl) localDataSource).findElement(DasUtil.getTables(localDataSource).get(0));
//
//
//        DatabaseView databaseView = DatabaseView.getDatabaseView(e.getProject());
//
//        Iterator<DbElement> iterator = DatabaseView.getSelectedElements(e.getDataContext(), DbElement.class).iterator();
//
//        ToolWindowManager manager = ToolWindowManager.getInstance(e.getProject());
//
//        // 打开或关闭Database面板
////        actionManager.getAction(ActivateToolWindowAction.getActionIdForToolWindow("Database")).actionPerformed(e);
//
//
//        // 模拟执行动作
////        anAction.actionPerformed(e);
//
//        // 主动打开Dialog
//        try {
//            Method method = DatabaseObjectRefactoring.class.getDeclaredMethod("showDialog", PsiElement.class, DasObject.class, Consumer.class);
//            method.setAccessible(true);
//            method.invoke(null, psiElement2, psiElement2, (Consumer<AbstractDbRefactoringDialog>) abstractDbRefactoringDialog -> {
//                //成功拿到Dialog实例对象
//                DbTableDialog dbTableDialog = (DbTableDialog) abstractDbRefactoringDialog;
//                System.out.println(dbTableDialog);
//                // 打开dialog完毕
//                dbTableDialog.finishAndShow();
//            });
//        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e1) {
//            e1.printStackTrace();
//        }
//
//
////        Messages.showInfoMessage("测试完毕！", "一个温馨的提示框跟你说：");
//    }
//}
