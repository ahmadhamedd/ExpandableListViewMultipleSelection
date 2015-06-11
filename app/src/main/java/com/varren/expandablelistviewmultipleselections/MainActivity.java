package com.varren.expandablelistviewmultipleselections;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ExpandableListView listView = (ExpandableListView) findViewById(R.id.list_view);
        final List<String> schools = Arrays.asList("School 1", "School 2");
        final Map<String, List<String>> students = new HashMap<>();
        students.put(schools.get(0), Arrays.asList("student1", "student2", "student3"));
        students.put(schools.get(1), Arrays.asList("student4", "student5", "student6"));

        final MyAdapter adapter = new MyAdapter(schools, students);
        listView.setAdapter(adapter);

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                adapter.toggleSelection(groupPosition, childPosition);
                adapter.notifyDataSetInvalidated();
                prettyPrintSelected(adapter);

                return false;
            }
        });
    }

    private class MyAdapter<G, C> extends BaseExpandableListAdapter {
        private List<G> groups;
        private Map<G, List<C>> childMap;
        private Map<G, List<C>> selectedItems;

        public MyAdapter(List<G> groups, Map<G, List<C>> childMap){

            this.groups = groups;
            this.childMap = childMap;

            //init selected Items Array
            this.selectedItems = new HashMap<>();

        }

        public Map<G, List<C>> getSelectedItems() {
            return selectedItems;
        }

        public boolean isSelected(int groupPosition, int childPosition){
            G group = groups.get(groupPosition);
            C child = getChild(groupPosition,childPosition);
            List<C> sel = selectedItems.get(group);
            return sel != null && sel.contains(child);
        }

        public void toggleSelection(int groupPosition, int childPosition){
            G group = groups.get(groupPosition);
            C child = getChild(groupPosition,childPosition);

            List<C> sel = selectedItems.get(group);
            if (sel == null){
                sel = new ArrayList<>();
                selectedItems.put(group, sel);
            }

            if (sel.contains(child))
                sel.remove(child);
            else
                sel.add(child);

        }

        @Override
        public int getGroupCount() {
            return groups.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            G group = getGroup(groupPosition);
            List<C> childList = childMap.get(group);
            int childCount = childList == null ? 0:childList.size();
            return childCount;
        }

        @Override
        public G getGroup(int groupPosition) {
            return groups.get(groupPosition);
        }

        @Override
        public C getChild(int groupPosition, int childPosition) {
            G group = getGroup(groupPosition);
            return childMap.get(group).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView node = (TextView) convertView;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater)MainActivity.this.getSystemService
                        (Context.LAYOUT_INFLATER_SERVICE);
                node = (TextView) inflater.inflate(R.layout.list_item, null);
            }

            node.setText(getGroup(groupPosition).toString());

            return node;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            TextView node = (TextView) convertView;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater)MainActivity.this.getSystemService
                        (Context.LAYOUT_INFLATER_SERVICE);
                node = (TextView) inflater.inflate(R.layout.list_item, null);
            }

            node.setText(getChild(groupPosition, childPosition).toString());
            if (isSelected(groupPosition,childPosition)){
                node.setBackgroundColor(Color.YELLOW);
            } else{
                node.setBackgroundColor(Color.WHITE);
            }
            return node;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    private void prettyPrintSelected(MyAdapter adapter) {
        Map<String, List<String>> selectedItems =  adapter.getSelectedItems();
        String result = "  \nSelected Items in list: \n";
        for(String school: selectedItems.keySet()){
            List<String> students = selectedItems.get(school);
            if (students.size() > 0){
                result+="\n" + school + ": ";
                for (String student: students){
                    result += student +", ";
                }
            }
        }

        Log.e("Selected Items", result);
    }
}
