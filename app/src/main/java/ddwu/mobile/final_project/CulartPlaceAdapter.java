package ddwu.mobile.final_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CulartPlaceAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context context;
    private int layout;
    private ArrayList<CulartPlaceDTO> list;

    public CulartPlaceAdapter(Context context, int layout, ArrayList<CulartPlaceDTO> list) {
        this.context = context;
        this.layout = layout;
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CulartPlaceDTO getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).get_id();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder = null;

        if (view == null) {
            view = inflater.inflate(layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvCulartPlaceName = view.findViewById(R.id.tvCulartPlaceName);
            viewHolder.tvCulartPlaceTel = view.findViewById(R.id.tvCulartPlaceTel);
            view.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) view.getTag();

        CulartPlaceDTO dto = list.get(position);

        viewHolder.tvCulartPlaceName.setText(dto.getName());
        viewHolder.tvCulartPlaceTel.setText(dto.getTel());

        return view;
    }

    public void setList(ArrayList<CulartPlaceDTO> list) {
        this.list = list;
    }

    static class ViewHolder {
        public TextView tvCulartPlaceName = null;
        public TextView tvCulartPlaceTel = null;
    }

}