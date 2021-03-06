package cn.itcast.erp.auth.menu.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import cn.itcast.erp.auth.menu.business.ebi.MenuEbi;
import cn.itcast.erp.auth.menu.vo.MenuModel;
import cn.itcast.erp.auth.menu.vo.MenuQueryModel;
import cn.itcast.erp.auth.role.business.ebi.RoleEbi;
import cn.itcast.erp.auth.role.vo.RoleModel;
import cn.itcast.erp.util.base.BaseAction;

public class MenuAction extends BaseAction {
	public MenuModel mm = new MenuModel();
	public MenuQueryModel mqm = new MenuQueryModel();

	private MenuEbi menuEbi;
	private RoleEbi roleEbi;

	public void setRoleEbi(RoleEbi roleEbi) {
		this.roleEbi = roleEbi;
	}

	public void setMenuEbi(MenuEbi menuEbi) {
		this.menuEbi = menuEbi;
	}

	// 列表
	public String list() {
		// 加载所有的一级菜单
		List<MenuModel> parentList = menuEbi.getAllOneLevel();
		put("parentList", parentList);
		setDataTotal(menuEbi.getCount(mqm));
		List<MenuModel> menuList = menuEbi.getAll(mqm, maxPageNum, pageCount);
		put("menuList", menuList);
		return LIST;
	}

	// 到添加
	public String input() {
		// 加载所有角色信息
		List<RoleModel> roleList = roleEbi.getAll();
		put("roleList", roleList);
		// 加载所有的一级菜单
		List<MenuModel> menuList = menuEbi.getAllOneLevel();
		put("menuList", menuList);
		if (mm.getUuid() != null) {
			mm = menuEbi.get(mm.getUuid());
			// set->array
			roleUuids = new Long[mm.getRoles().size()];
			int i = 0;
			for (RoleModel rm : mm.getRoles()) {
				roleUuids[i++] = rm.getUuid();
			}
		}
		return INPUT;
	}

	public Long[] roleUuids;

	// 添加
	public String save() {
		if (mm.getUuid() == null) {
			menuEbi.save(mm, roleUuids);
		} else {
			menuEbi.update(mm, roleUuids);
		}
		return TO_LIST;
	}
	// 删除
	public String delete() {
		menuEbi.delete(mm);
		return TO_LIST;
	}

	// 显示菜单
	public void showMenu() throws IOException {
		// 1.首先获取root参数
		String root = getRequest().getParameter("root");
		// 2.判断参数值 source id
		HttpServletResponse response = getResponse();

		response.setContentType("text/html;charset=utf-8");
		PrintWriter pw = response.getWriter();

		StringBuilder json = new StringBuilder();
		json.append("[");

		if ("source".equals(root)) {
			// 生成一级菜单
			List<MenuModel> menuList = menuEbi.getAllOneLevelByEmp(getLogin()
					.getUuid());
			for (MenuModel temp : menuList) {
				json.append("{\"text\":\"");
				json.append(temp.getName());
				json.append("\",\"hasChildren\":true,\"classes\":\"folder\",\"id\":\"");
				json.append(temp.getUuid());
				json.append("\"},");
			}
		} else {
			// 生成二级菜单项
			// 获取指定一级菜单的二级菜单项
			Long puuid = new Long(root);
			List<MenuModel> menuList = menuEbi.getByEmpAndPuuid(getLogin()
					.getUuid(), puuid);
			for (MenuModel temp : menuList) {
				json.append("{\"text\":\"<a class='hei' target='main' href='");
				json.append(temp.getUrl());
				json.append("'>");
				json.append(temp.getName());
				json.append("</a>\",\"hasChildren\":false,\"classes\":\"file\"},");
			}
		}

		json.deleteCharAt(json.length() - 1);
		json.append("]");

		pw.write(json.toString());
		pw.flush();
	}

	// public void showMenu() throws IOException {
	// HttpServletResponse response = getResponse();
	// // 手动设置字符集
	// response.setContentType("text/html;charset=utf-8");
	// PrintWriter pw = response.getWriter();
	//
	// StringBuilder json = new StringBuilder();
	// json.append("[");
	//
	// // 获取当前登录人所能进行操作的所有一级菜单
	// List<MenuModel> menuList = menuEbi.getAllOneLevelByEmp(getLogin()
	// .getUuid());
	// for (MenuModel temp : menuList) {
	// json.append("{\"text\":\"");
	// json.append(temp.getName());
	// json.append("\",\"hasChildren\":true,\"classes\":\"folder\",\"id\":\"");
	// json.append(temp.getUuid());
	// json.append("\"},");
	// }
	// json.deleteCharAt(json.length() - 1);
	// json.append("]");
	//
	// pw.write(json.toString());
	// pw.flush();
	// }

	// 显示菜单
	// public String showMenu() throws IOException {
	// // 返回一个JSON数据即可
	// // 从request中获取参数root的值
	// // 判断是不是source
	// // 如果是source，返回json数组1
	// // 否则返回json数组2
	//
	// // 在struts2的响应的方法中如何返回一个json数据
	// // 写出json
	// String json = "[{\"aa\":1}]";
	// // 使用原始ServletAPI完成自定义的JSON内容写入响应
	// HttpServletResponse responsed = getResponse();
	// PrintWriter pw = responsed.getWriter();
	// pw.write(json);
	// pw.flush();
	//
	// // 如果设置当前返回Json,通过手工形式将Json写入请求，返回null，NONE
	// return null;
	// }

}
