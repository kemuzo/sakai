package org.sakaiproject.delegatedaccess.tool.pages;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.tree.TreeNode;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.sakaiproject.delegatedaccess.model.NodeModel;

public class EditablePanelDate  extends Panel{
	
	SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy");

	public EditablePanelDate(String id, IModel inputModel, final NodeModel nodeModel, final TreeNode node, final boolean startDate)
	{
		super(id);
		final DateTextField date = new DateTextField("dateTextField", inputModel, format.toPattern()){
			@Override
			public boolean isVisible() {
				return nodeModel.isDirectAccess();
			}
		};
		date.add(new AjaxFormComponentUpdatingBehavior("onchange")
        {
            @Override
            protected void onUpdate(AjaxRequestTarget target)
            {
            	DateTextField newDate = date;
            	if(startDate){
            		nodeModel.setShoppingPeriodStartDate(date.getModelObject());
            	}else{
            		nodeModel.setShoppingPeriodEndDate(date.getModelObject());
            	}
            	
            	
            	//In order for the models to refresh, you have to call "expand" or "collapse" then "updateTree",
				//since I don't want to expand or collapse, I just call whichever one the node is already
				//Refreshing the tree will update all the models and information (like role) will be generated onClick
				if(((BaseTreePage)target.getPage()).getTree().getTreeState().isNodeExpanded(node)){
					((BaseTreePage)target.getPage()).getTree().getTreeState().expandNode(node);
				}else{
					((BaseTreePage)target.getPage()).getTree().getTreeState().collapseNode(node);
				}
				((BaseTreePage)target.getPage()).getTree().updateTree(target);
            }
            
        });
		add(date);
		
		IModel<String> labelModel = new AbstractReadOnlyModel<String>() {
			@Override
			public String getObject() {
				Date date = null;
				if(startDate)
					date = nodeModel.getInheritedShoppingPeriodStartDate();
				else
					date = nodeModel.getInheritedShoppingPeriodEndDate();
				if(date == null){
					return "";
				}else{
					return format.format(date);
				}
			}
		};
		add(new Label("inherited", labelModel){
			public boolean isVisible() {
				return !nodeModel.isDirectAccess();
			};
		});
	}

}
