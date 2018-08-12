package com.chromaclypse.lib.bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

import com.chromaclypse.api.messages.Formatter;

public class FormatterBukkit implements Formatter {
	private final static char colorChar = ChatColor.COLOR_CHAR;

	@Override
	public boolean isColor(String input, int index) {
		try {
			return input.charAt(index) == colorChar;
		}
		catch(IndexOutOfBoundsException e) {
			return false;
		}
	}
	
	@Override
	public String colorize(String input) {
		return ChatColor.translateAlternateColorCodes('&', input);
	}
	
	@Override
	public String colorize(String... inputs) {
		StringBuilder sb = new StringBuilder();
		
		for(String input : inputs)
			sb.append(colorize(input));
		
		return sb.toString();
	}
	
	@Override
	public String[] colorizeList(String... inputs) {
		String[] output = new String[inputs.length];
		
		for(int i = 0; i < inputs.length; ++i)
			output[i] = colorize(inputs[i]);
		
		return output;
	}

	@Override
	public String decolor(String input) {
		return ChatColor.stripColor(colorize(input));
	}
	
	@Override
	public String decolor(String... inputs) {
		StringBuilder sb = new StringBuilder();

		for(String input : inputs)
			sb.append(decolor(input));
		
		return sb.toString();
	}
	
	@Override
	public String[] decolorList(String... inputs) {
		String[] output = new String[inputs.length];
		
		for(int i = 0; i < inputs.length; ++i)
			output[i] = decolor(inputs[i]);
		
		return output;
	}

	@Override
	public String niceName(String input) {
		String Output = input.replace('_', ' ').trim().toLowerCase();
		Pattern p = Pattern.compile("\\b[a-z]");
		Matcher m = p.matcher(Output);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, Output.substring(m.start(), m.end()).toUpperCase());
		}
		m.appendTail(sb);
		
		return sb.toString();
	}
	
	@Override
	public List<String> wrap(int max_length, String... input) {
		List<String> output = new ArrayList<>(input.length);
		max_length = Math.max(max_length, 8);
		String format = "";
		for(String s1 : input) {
			if(s1 == null) {
				continue;
			}
			
			for(String s : s1.split("\\\\n")) {
				int begin = 0;
				int end = -1;
				int seq_begin = 0;
				int seq_end = -1;
				String prepared_format = format;
				String committed_format = format;
				for(int i = 0, n = 0; i < s.length(); ++i) {
					char c1 = s.charAt(i);
					if(c1 == colorChar)
						if(i + 1 != s.length()) {
							char c = Character.toLowerCase(s.charAt(i + 1));
							if("0123456789abcdefr".indexOf(c) != -1)  {
								prepared_format = String.valueOf(colorChar) + c;
								n -= 2;
								if(i > seq_end)
									seq_begin = i;
								seq_end = i+1;
							}
							else if("olmnk".indexOf(c) != -1) {
								prepared_format += String.valueOf(colorChar) + c;
								n -= 2;
								if(i > seq_end)
									seq_begin = i;
								seq_end = i+1;
							}
						}
					
					if(c1 == ' ' && i > 0)
						end = i;
					
					if(n == max_length) {
						if(end == -1) {
							if(i-2 == seq_end || i-1 == seq_end)
								end = seq_begin + i - seq_end - 2;
							else
								end = i - 1;
	
							output.add(format + s.substring(begin, end) + '-');
						}
						else
							output.add(format + s.substring(begin, end));
						
						begin = end;
						n = i - end;
						end = -1;
						format = committed_format;
					}
					else
						++n;
					
					if(i > seq_end)
						committed_format = prepared_format;
				}
				output.add(format + s.substring(begin));
				format = prepared_format;
			}
		}
		
		return output;
	}
}
