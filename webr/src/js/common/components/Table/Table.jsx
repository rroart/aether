/* eslint-disable no-undef */
import React, { memo } from 'react';
import { useTable } from 'react-table'
import ReactTooltip from "react-tooltip";

function Table({ hcolumns, hdata }) {
    //console.log("callme");
    if (hcolumns == null || hdata == null || hcolumns.length == 0 || hdata.length == 0) {
	return (
	    <h2>No table</h2>
	);
    }
    const {
	getTableProps,
	getTableBodyProps,
	headerGroups,
	rows,
	prepareRow,
    } = useTable({ columns: hcolumns, data: hdata });
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
