function donate_shortcode( $atts ) {  
         extract(shortcode_atts(array(  
             'text' => 'Make a donation',  
             'account' => 'REPLACE ME',  
             'for' => '',  
         ), $atts));  
         global $post;  
         if (!$for) $for = str_replace(" ","+",$post->post_title);  
         return '<a class="donateLink" href="https://www.paypal.com/cgi-bin/webscr?cmd=_xclick&business='.$account.'&item_name=Donation+for+'.$for.'">'.$text.'</a>';  
     }  
     add_shortcode('donate', 'donate_shortcode')
