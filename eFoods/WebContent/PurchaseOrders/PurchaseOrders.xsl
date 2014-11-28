<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<html>
			<head>
				<title>Purchase Order Report</title>
			</head>
			<body>
				<h1> Order Information </h1>
				<xsl:apply-templates/>
    		</body>
    	</html>
    </xsl:template>
    
    <xsl:template match="order">
    	<div>
    		<p>ID: <xsl:value-of select="@id"/></p>
    		<p>Time Submitted: <xsl:value-of select="@submitted"/></p>
    	</div>
    	
    	<xsl:apply-templates select="customer"/>
    	<table border="1" cellpadding="5">
    		<tr>
    			<th>Item Number</th>
    			<th>Item Name</th>
    			<th>Price</th>
    			<th>Quantity</th>
    			<th>Extended</th>
    		</tr>
    		<xsl:for-each select="./items/item">
    			<tr>
    				<td><xsl:value-of select="@number"/></td>
		   			<td><xsl:value-of select="./name"/></td>
					<td><xsl:value-of select="./price"/></td>
					<td><xsl:value-of select="./quantity"/></td>
					<td><xsl:value-of select="./extended"/></td>
				</tr>
   			</xsl:for-each>
   		</table>
    	
    	<h3> Costs </h3>
    	<p>Total: <xsl:value-of select="./total"/></p>
    	<p>Shipping: <xsl:value-of select="./shipping"/></p>
    	<p>HST: <xsl:value-of select="./HST"/></p>
    	<p>Grand Total: <xsl:value-of select="./grandTotal"/></p>  		 	
    </xsl:template>
    
    <xsl:template match="customer">
    	<p>Customer Name: <xsl:value-of select="./name"/></p>
    </xsl:template>
    
</xsl:stylesheet>