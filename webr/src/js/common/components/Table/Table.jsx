/* eslint-disable no-undef */
import React, { memo, useEffect, useMemo } from 'react';
import { useTable } from 'react-table'
import ReactTooltip from "react-tooltip";

function Table({ columns, data }) {
    console.log("callme");
    console.log(columns);
    console.log(data);
    if (columns == null || data == null || columns.length == 0 || data.length == 0) {
	console.log("callmein");
	return (
	    <h2>No table</h2>
	);
    }
    const hcolumns = useMemo( () => columns); //, [mycolumns] );
    const hdata = useMemo( () => data);//, [mydata] );
    const {
	getTableProps,
	getTableBodyProps,
	headerGroups,
	rows,
	prepareRow,
    } = useTable({ columns: hcolumns, data: hdata });
    useEffect(() => {
        ReactTooltip.rebuild()
    });
    //console.log("callme");
    return (
	<div>
	    <table {...getTableProps()}>
		<thead>
		    {// Loop over the header rows
			headerGroups.map(headerGroup => (
			    // Apply the header row props
			    <tr {...headerGroup.getHeaderGroupProps()}>
				{// Loop over the headers in each row
				    headerGroup.headers.map(column => (
					// Apply the header cell props
					<th {...column.getHeaderProps()}>
					    {// Render the header
						column.render('Header')}
					</th>
				    ))}
			    </tr>
			))}
		</thead>
		{/* Apply the table body props */}
		<tbody {...getTableBodyProps()}>
		    {// Loop over the table rows
			rows.map(row => {
			    // Prepare the row for display
			    prepareRow(row)
			    return (
				// Apply the row props
				<tr {...row.getRowProps()}>
				    {// Loop over the rows cells
					row.cells.map(cell => {
					    // Apply the cell props
					    return (
						<td {...cell.getCellProps()}>
						    {// Render the cell contents
							cell.render('Cell')}
						</td>
					    )
					})}
				</tr>
			    )
			})}
		</tbody>
	    </table>
	</div>
    );
}

export default memo(Table);
