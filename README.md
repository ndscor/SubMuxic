SubMuxic for SubSonic Server

SubMuxic is simple , Material Designed Music Streaming App for SubSonic Server.

SubMuxic is licensed under the terms of the GNU General Public License version 3 (GPLv3).

https://opensource.org/licenses/gpl-3.0.html

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
     


![1598366046281](https://user-images.githubusercontent.com/5296601/91191712-bf1d0880-e712-11ea-8a7b-ec64620342ef.png)

![1598366041824](https://user-images.githubusercontent.com/5296601/91192075-28048080-e713-11ea-9401-1290d2f81d40.png)


![1598363392120](https://user-images.githubusercontent.com/5296601/91191846-e673d580-e712-11ea-91c2-3af19d72a2ab.png)

![1598363398583](https://user-images.githubusercontent.com/5296601/91191861-ea9ff300-e712-11ea-96ba-d9f249113227.png)

![1598363403992](https://user-images.githubusercontent.com/5296601/91191870-ed024d00-e712-11ea-99aa-d5a9347b691e.png)

![1598363414104](https://user-images.githubusercontent.com/5296601/91191963-06a39480-e713-11ea-92b5-6ee3ec42d9b0.png)

![1598363436604](https://user-images.githubusercontent.com/5296601/91191969-086d5800-e713-11ea-9bd2-06d7ca49dfed.png)
